package com.api.hotifi.payment.services.implementations;

import com.api.hotifi.common.constants.configurations.AppConfigurations;
import com.api.hotifi.common.exception.HotifiException;
import com.api.hotifi.common.services.interfaces.IEmailService;
import com.api.hotifi.common.utils.LegitUtils;
import com.api.hotifi.identity.entities.User;
import com.api.hotifi.identity.errors.UserErrorCodes;
import com.api.hotifi.identity.models.EmailModel;
import com.api.hotifi.identity.repositories.UserRepository;
import com.api.hotifi.payment.entities.BankAccount;
import com.api.hotifi.payment.error.SellerBankAccountErrorCodes;
import com.api.hotifi.payment.repositories.BankAccountRepository;
import com.api.hotifi.payment.services.interfaces.IBankAccountService;
import com.api.hotifi.payment.web.request.BankAccountRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Slf4j
public class BankAccountServiceImpl implements IBankAccountService {

    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;
    private final IEmailService emailService;

    public BankAccountServiceImpl(UserRepository userRepository, BankAccountRepository bankAccountRepository, IEmailService emailService) {
        this.userRepository = userRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.emailService = emailService;
    }

    @Transactional
    @Override
    public void addBankAccount(BankAccountRequest bankAccountRequest) {
        if (bankAccountRequest.getErrorDescription() != null)
            throw new HotifiException(SellerBankAccountErrorCodes.ERROR_DESCRIPTION_ON_CREATION_BY_SELLER);
        User seller = userRepository.findById(bankAccountRequest.getUserId()).orElse(null);
        if (seller == null)
            throw new HotifiException(UserErrorCodes.USER_NOT_FOUND);
        if (seller.getAuthentication().isDeleted())
            throw new HotifiException(UserErrorCodes.USER_DELETED);
        if (!seller.isLoggedIn())
            throw new HotifiException(UserErrorCodes.USER_NOT_LOGGED_IN);
        try {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBankAccountType(bankAccountRequest.getBankAccountType());
            bankAccount.setAccountType(bankAccountRequest.getAccountType());
            bankAccount.setBankIfscCode(bankAccountRequest.getBankIfscCode());
            bankAccount.setBankAccountNumber(bankAccountRequest.getBankAccountNumber());
            bankAccount.setBankBeneficiaryName(bankAccountRequest.getBankBeneficiaryName());
            bankAccount.setUser(seller);
            seller.setBankAccount(bankAccount);
            bankAccountRepository.save(bankAccount);
            userRepository.save(seller);
        } catch (DataIntegrityViolationException e) {
            throw new HotifiException(SellerBankAccountErrorCodes.BANK_ACCOUNT_DETAILS_ALREADY_EXISTS);
        } catch (Exception e) {
            throw new HotifiException(SellerBankAccountErrorCodes.UNEXPECTED_SELLER_BANK_ACCOUNT_ERROR);
        }
    }

    @Transactional
    @Override
    public void updateBankAccountByCustomer(BankAccountRequest bankAccountRequest) {
        User seller = userRepository.findById(bankAccountRequest.getUserId()).orElse(null);
        if (LegitUtils.isSellerLegit(seller, false)) {
            try {
                Date modifiedAt = new Date(System.currentTimeMillis());
                BankAccount bankAccount = seller.getBankAccount();
                bankAccount.setUser(seller);
                bankAccount.setBankAccountType(bankAccountRequest.getBankAccountType());
                bankAccount.setModifiedAt(modifiedAt);
                bankAccount.setAccountType(bankAccountRequest.getAccountType());
                bankAccount.setBankIfscCode(bankAccountRequest.getBankIfscCode());
                bankAccount.setBankAccountNumber(bankAccountRequest.getBankAccountNumber());
                bankAccount.setBankBeneficiaryName(bankAccountRequest.getBankBeneficiaryName());
                bankAccount.setLinkedAccountId(null);
                bankAccountRepository.save(bankAccount);
            } catch (DataIntegrityViolationException e) {
                throw new HotifiException(SellerBankAccountErrorCodes.BANK_ACCOUNT_DETAILS_ALREADY_EXISTS);
            } catch (Exception e) {
                throw new HotifiException(SellerBankAccountErrorCodes.UNEXPECTED_SELLER_BANK_ACCOUNT_ERROR);
            }
        }
    }

    @Transactional
    @Override
    public void updateBankAccountByAdmin(Long userId, String linkedAccountId, String errorDescription) {
        User user = userRepository.findById(userId).orElse(null);
        if (LegitUtils.isSellerLegitByAdmin(user, linkedAccountId, errorDescription)) {
            try {
                Date modifiedAt = new Date(System.currentTimeMillis());
                BankAccount bankAccount = user.getBankAccount();
                bankAccount.setLinkedAccountId(linkedAccountId);
                bankAccount.setModifiedAt(modifiedAt);
                bankAccount.setErrorDescription(errorDescription);
                bankAccount.setUser(user);
                bankAccountRepository.save(bankAccount);

                EmailModel emailModel = new EmailModel();
                emailModel.setToEmail(user.getAuthentication().getEmail());
                emailModel.setFromEmail(AppConfigurations.FROM_EMAIL);
                emailModel.setFromEmailPassword(AppConfigurations.FROM_EMAIL_PASSWORD);

                if (errorDescription != null)
                    emailService.sendLinkedAccountFailed(user, errorDescription, emailModel);
                emailService.sendLinkedAccountSuccessEmail(user, emailModel);

            } catch (DataIntegrityViolationException e) {
                throw new HotifiException(SellerBankAccountErrorCodes.BANK_ACCOUNT_DETAILS_ALREADY_EXISTS);
            } catch (Exception e) {
                throw new HotifiException(SellerBankAccountErrorCodes.UNEXPECTED_SELLER_BANK_ACCOUNT_ERROR);
            }
        }
    }

    @Transactional
    @Override
    public BankAccount getBankAccountByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        return user != null ? user.getBankAccount() : null;
    }

    @Transactional
    @Override
    public List<BankAccount> getUnlinkedBankAccounts() {
        return bankAccountRepository.findUnverifiedBankAccounts();
    }
}
