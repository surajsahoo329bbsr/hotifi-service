package com.api.hotifi.payment.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RatingValidator implements ConstraintValidator<Rating, Float> {

    List<Float> ratings;

    @Override
    public void initialize(Rating rating) {
        this.ratings = new ArrayList<>(Arrays.asList(1.0F, 1.5F, 2.0F, 2.5F, 3.0F, 3.5F, 4.0F, 4.5F, 5.0F));
    }

    @Override
    public boolean isValid(Float rating, ConstraintValidatorContext constraintValidatorContext) {
        return rating != null && ratings.contains(rating);
    }
}
