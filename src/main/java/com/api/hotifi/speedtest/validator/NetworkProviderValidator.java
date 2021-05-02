package com.api.hotifi.speedtest.validator;

import com.api.hotifi.speedtest.codes.NetworkProviderCodes;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NetworkProviderValidator implements ConstraintValidator<NetworkProvider, String> {

    List<String> networkProviders;

    @Override
    public void initialize(NetworkProvider constraintAnnotation) {
        networkProviders = Stream.of(NetworkProviderCodes.values()).map(NetworkProviderCodes::name).collect(Collectors.toList());
    }

    @Override
    public boolean isValid(String accountType, ConstraintValidatorContext constraintValidatorContext) {
        return accountType != null && networkProviders.contains(accountType);
    }
}
