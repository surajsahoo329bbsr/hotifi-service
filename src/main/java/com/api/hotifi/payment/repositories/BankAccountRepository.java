package com.api.hotifi.payment.repositories;

import com.api.hotifi.payment.entities.BankAccount;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BankAccountRepository extends PagingAndSortingRepository<BankAccount, Long> {

    @Query(value = "SELECT * FROM bank_account WHERE linked_account_id IS NULL AND error_description IS NULL", nativeQuery = true)
    List<BankAccount> findUnverifiedBankAccounts();

}
