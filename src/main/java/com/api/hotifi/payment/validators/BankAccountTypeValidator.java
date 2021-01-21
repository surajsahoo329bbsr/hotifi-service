package com.api.hotifi.payment.validators;

import com.api.hotifi.payment.processor.codes.BankAccountTypeCodes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BankAccountTypeValidator implements ConstraintValidator<BankAccountType, String> {

    List<String> bankAccountTypes;

    @Override
    public void initialize(BankAccountType constraintAnnotation) {
        bankAccountTypes = Stream.of(BankAccountTypeCodes.values()).map(BankAccountTypeCodes::name).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String bankAccountType, ConstraintValidatorContext constraintValidatorContext) {
        return bankAccountType != null && bankAccountTypes.contains(bankAccountType);
    }
}