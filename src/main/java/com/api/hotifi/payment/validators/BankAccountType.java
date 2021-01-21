package com.api.hotifi.payment.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = BankAccountTypeValidator.class)
public @interface BankAccountType {
    String message() default "Value must be one in {CURRENT_ACCOUNT, SAVINGS_ACCOUNT, SALARY_ACCOUNT, FIXED_DEPOSIT_ACCOUNT, RECURRING_DEPOSIT_ACCOUNT, NRI_ACCOUNT, DEMAT_ACCOUNT, OTHERS}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
