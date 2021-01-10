package com.api.hotifi.payment.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UpiAppValidator.class)
public @interface UpiApp {
    String message() default "Value must be one in {GOOGLE_PAY, PHONE_PE, PAYTM, OTHERS}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
