package com.api.hotifi.payment.validators;

import com.api.hotifi.payment.processor.codes.AccountTypeCodes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AccountTypeValidator implements ConstraintValidator<AccountType, String> {

    List<String> accountTypes;

    @Override
    public void initialize(AccountType constraintAnnotation) {
        accountTypes = Stream.of(AccountTypeCodes.values()).map(AccountTypeCodes::name).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String accountType, ConstraintValidatorContext constraintValidatorContext) {
        return accountType != null && accountTypes.contains(accountType);
    }
}
