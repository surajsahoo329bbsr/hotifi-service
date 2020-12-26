package com.api.hotifi.payment.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = RatingValidator.class)
public @interface Rating {

    String message() default "Value must be one in {1, 1.5, 2, 2.5, 3, 3.5, 4, 4.5, 5}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
