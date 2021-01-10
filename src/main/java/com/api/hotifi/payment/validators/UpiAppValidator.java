package com.api.hotifi.payment.validators;

import com.api.hotifi.payment.processor.codes.UpiPaymentCodes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UpiAppValidator implements ConstraintValidator<UpiApp, String> {

    List<String> upiApps;

    @Override
    public void initialize(UpiApp constraintAnnotation) {
        upiApps = Stream.of(UpiPaymentCodes.values()).map(UpiPaymentCodes::name).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String upiApp, ConstraintValidatorContext constraintValidatorContext) {
        return upiApp == null || upiApps.contains(upiApp);
    }
}
