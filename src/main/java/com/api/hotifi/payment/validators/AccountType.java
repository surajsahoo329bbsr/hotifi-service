package com.api.hotifi.payment.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = AccountTypeValidator.class)
public @interface AccountType {

    String message() default "Value must be one in {PRIVATE_LIMITED, PROPRIETORSHIP, PARTNERSHIP, INDIVIDUAL, PUBLIC_LIMITED, LLP, TRUST, SOCIETY, NGO}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
