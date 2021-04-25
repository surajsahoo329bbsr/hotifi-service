package com.api.hotifi.payment.services.interfaces;

import com.api.hotifi.payment.entities.BankAccount;
import com.api.hotifi.payment.web.request.BankAccountRequest;
import com.api.hotifi.payment.web.responses.BankAccountAdminResponse;

import java.util.List;

public interface IBankAccountService {

    void addBankAccount(BankAccountRequest bankAccountRequest);

    void updateBankAccountByCustomer(BankAccountRequest bankAccountRequest);

    void updateBankAccountByAdmin(Long sellerId, String linkedAccountId, String errorDescription);

    BankAccount getBankAccountByUserId(Long sellerId);

    List<BankAccountAdminResponse> getUnlinkedBankAccounts();

}
