package com.api.hotifi.payment.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PaymentMethodValidator.class)
public @interface PaymentMethod {

    String message() default "Value must be one in {UPI_PAYMENT, NETBANKING_PAYMENT, DEBIT_CARD_PAYMENT, CREDIT_CARD_PAYMENT}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
