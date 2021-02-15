package com.api.hotifi.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SocialClientValidator.class)
public @interface SocialClient {

    String message() default "Value must be one in {GOOGLE, FACEBOOK, TWITTER, GITHUB, MICROSOFT, APPLE}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
