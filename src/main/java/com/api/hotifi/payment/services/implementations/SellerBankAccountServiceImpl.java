package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.SellerBankAccount;
import com.api.hotifi.payment.error.SellerBankAccountErrorCodes;
import com.api.hotifi.payment.repositories.SellerBankAccountRepository;
import com.api.hotifi.payment.services.interfaces.ISellerBankAccountService;
import com.api.hotifi.payment.web.request.SellerBankAccountRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class SellerBankAccountServiceImpl implements ISellerBankAccountService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerBankAccountRepository sellerBankAccountRepository;

    @Transactional
    @Override
    public void addSellerBankAccount(SellerBankAccountRequest sellerBankAccountRequest) {
        if (sellerBankAccountRequest.getErrorDescription() != null)
            throw new HotifiException(SellerBankAccountErrorCodes.ERROR_DESCRIPTION_ON_CREATION_BY_SELLER);
        User seller = userRepository.findById(sellerBankAccountRequest.getSellerId()).orElse(null);
        if (seller == null)
            throw new HotifiException(UserErrorCodes.NO_USER_EXISTS);
        if (seller.getAuthentication().isDeleted())
            throw new HotifiException(UserErrorCodes.USER_DELETED);
        if (!seller.isLoggedIn())
            throw new HotifiException(UserErrorCodes.USER_NOT_LOGGED_IN);
        try {
            SellerBankAccount sellerBankAccount = new SellerBankAccount();
            sellerBankAccount.setBankAccountType(sellerBankAccountRequest.getBankAccountType());
            sellerBankAccount.setAccountType(sellerBankAccountRequest.getAccountType());
            sellerBankAccount.setBankIfscCode(sellerBankAccountRequest.getBankIfscCode());
            sellerBankAccount.setBankAccountNumber(sellerBankAccountRequest.getBankAccountNumber());
            sellerBankAccount.setBankBeneficiaryName(sellerBankAccountRequest.getBankBeneficiaryName());
            sellerBankAccount.setUser(seller);
            seller.setSellerBankAccount(sellerBankAccount);
            sellerBankAccountRepository.save(sellerBankAccount);
            userRepository.save(seller);
        } catch (DataIntegrityViolationException e) {
            throw new HotifiException(SellerBankAccountErrorCodes.BANK_ACCOUNT_DETAILS_ALREADY_EXISTS);
        } catch (Exception e) {
            throw new HotifiException(SellerBankAccountErrorCodes.UNEXPECTED_SELLER_BANK_ACCOUNT_ERROR);
        }
    }

    @Transactional
    @Override
    public void updateSellerBankAccountBySeller(SellerBankAccountRequest sellerBankAccountRequest) {
        User seller = userRepository.findById(sellerBankAccountRequest.getSellerId()).orElse(null);
        if (LegitUtils.isSellerLegit(seller, false)) {
            try {
                Date modifiedAt = new Date(System.currentTimeMillis());
                SellerBankAccount sellerBankAccount = seller.getSellerBankAccount();
                sellerBankAccount.setUser(seller);
                sellerBankAccount.setBankAccountType(sellerBankAccountRequest.getBankAccountType());
                sellerBankAccount.setModifiedAt(modifiedAt);
                sellerBankAccount.setAccountType(sellerBankAccountRequest.getAccountType());
                sellerBankAccount.setBankIfscCode(sellerBankAccountRequest.getBankIfscCode());
                sellerBankAccount.setBankAccountNumber(sellerBankAccountRequest.getBankAccountNumber());
                sellerBankAccount.setBankBeneficiaryName(sellerBankAccountRequest.getBankBeneficiaryName());
                sellerBankAccount.setLinkedAccountId(null);
                sellerBankAccountRepository.save(sellerBankAccount);
            } catch (DataIntegrityViolationException e) {
                throw new HotifiException(SellerBankAccountErrorCodes.BANK_ACCOUNT_DETAILS_ALREADY_EXISTS);
            } catch (Exception e) {
                throw new HotifiException(SellerBankAccountErrorCodes.UNEXPECTED_SELLER_BANK_ACCOUNT_ERROR);
            }
        }
    }

    @Transactional
    @Override
    public void updateSellerBankAccountByAdmin(Long sellerId, String linkedAccountId, String errorDescription) {
        User seller = userRepository.findById(sellerId).orElse(null);
        if (LegitUtils.isSellerLegitByAdmin(seller, linkedAccountId, errorDescription)) {
            try {
                Date modifiedAt = new Date(System.currentTimeMillis());
                SellerBankAccount sellerBankAccount = seller.getSellerBankAccount();
                sellerBankAccount.setLinkedAccountId(linkedAccountId);
                sellerBankAccount.setModifiedAt(modifiedAt);
                sellerBankAccount.setErrorDescription(errorDescription);
                sellerBankAccount.setUser(seller);
                sellerBankAccountRepository.save(sellerBankAccount);
            } catch (DataIntegrityViolationException e) {
                throw new HotifiException(SellerBankAccountErrorCodes.BANK_ACCOUNT_DETAILS_ALREADY_EXISTS);
            } catch (Exception e) {
                throw new HotifiException(SellerBankAccountErrorCodes.UNEXPECTED_SELLER_BANK_ACCOUNT_ERROR);
            }
        }
    }

    @Transactional
    @Override
    public SellerBankAccount getSellerBankAccountBySellerId(Long sellerId) {
        User seller = userRepository.findById(sellerId).orElse(null);
        return seller != null ? seller.getSellerBankAccount() : null;
    }

    @Transactional
    @Override
    public List<SellerBankAccount> getUnlinkedSellerBankAccounts() {
        return sellerBankAccountRepository.findUnverifiedSellerBankAccounts();
    }
}
