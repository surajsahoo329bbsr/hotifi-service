package com.api.hotifi.payment.validators;

import com.api.hotifi.payment.processor.codes.PaymentMethodCodes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaymentMethodValidator implements ConstraintValidator<PaymentMethod, String> {

    List<String> paymentMethods;

    @Override
    public void initialize(PaymentMethod constraintAnnotation) {
        paymentMethods = Stream.of(PaymentMethodCodes.values()).map(PaymentMethodCodes::name).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String paymentMethod, ConstraintValidatorContext constraintValidatorContext) {
        return paymentMethod != null && paymentMethods.contains(paymentMethod);
    }
}
