package com.api.hotifi.payment.web.request;

import com.api.hotifi.payment.validators.AccountType;
import com.api.hotifi.payment.validators.BankAccountType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
public class BankAccountRequest {

    @Range(min = 1, message = "{seller.id.invalid}")
    private Long userId;

    @AccountType
    private String accountType;

    @NotBlank(message = "{bank.account.number.blank}")
    @Length(max = 255, message = "{bank.account.name.number.invalid}")
    private String bankAccountNumber;

    @BankAccountType
    private String bankAccountType;

    @NotBlank(message = "{bank.ifsc.code.blank}")
    @Length(max = 11, message = "{bank.ifsc.code.invalid}")
    private String bankIfscCode;

    @NotBlank(message = "{bank.beneficiary.name.blank}")
    @Length(max = 255, message = "{bank.beneficiary.name.invalid}")
    private String bankBeneficiaryName;

    @Length(max = 255, message = "{error.description.invalid}")
    private String errorDescription;

}
