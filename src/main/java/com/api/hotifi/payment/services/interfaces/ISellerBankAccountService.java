package com.api.hotifi.payment.services.interfaces;

import com.api.hotifi.payment.entities.SellerBankAccount;
import com.api.hotifi.payment.web.request.SellerBankAccountRequest;

import java.util.List;

public interface ISellerBankAccountService {

    void addSellerBankAccount(SellerBankAccountRequest sellerBankAccountRequest);

    void updateSellerBankAccountBySeller(SellerBankAccountRequest sellerBankAccountRequest);

    void updateSellerBankAccountByAdmin(Long sellerId, String linkedAccountId, String errorDescription);

    SellerBankAccount getSellerBankAccountBySellerId(Long sellerId);

    List<SellerBankAccount> getUnlinkedSellerBankAccounts();

}
