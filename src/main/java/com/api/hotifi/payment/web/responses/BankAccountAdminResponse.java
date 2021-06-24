package com.api.hotifi.payment.web.responses;

import com.api.hotifi.payment.entities.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class BankAccountAdminResponse {

    private final BankAccount bankAccount;

    private final Long userId;

    private final String email;

}
