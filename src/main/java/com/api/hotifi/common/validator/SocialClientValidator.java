package com.api.hotifi.common.validator;

import com.api.hotifi.common.processors.codes.SocialCodes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SocialClientValidator implements ConstraintValidator<SocialClient, String> {

    List<String> socialClients;

    @Override
    public void initialize(SocialClient constraintAnnotation) {
        socialClients = Stream.of(SocialCodes.values()).map(SocialCodes::name).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String paymentMethod, ConstraintValidatorContext constraintValidatorContext) {
        return paymentMethod != null && socialClients.contains(paymentMethod);
    }
}