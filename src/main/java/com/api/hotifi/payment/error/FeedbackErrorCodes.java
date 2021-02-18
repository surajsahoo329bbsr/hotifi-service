package com.api.hotifi.payment.error;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class FeedbackErrorCodes extends ErrorCodes {
    public static final ErrorCode UNEXPECTED_FEEDBACK_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_FEEDBACK_ERROR", FeedbackErrorMessages.UNEXPECTED_FEEDBACK_ERROR)), 500);
    public static final ErrorCode FEEDBACK_ALREADY_GIVEN = new ErrorCode("01", Collections.singletonList(getMessage("FEEDBACK_ALREADY_GIVEN", FeedbackErrorMessages.FEEDBACK_ALREADY_GIVEN)), 500);
    public static final ErrorCode PURCHASE_NOT_FOUND_FOR_FEEDBACK = new ErrorCode("02", Collections.singletonList(getMessage("PURCHASE_NOT_FOUND_FOR_FEEDBACK", FeedbackErrorMessages.PURCHASE_NOT_FOUND_FOR_FEEDBACK)), 500);
    public static final ErrorCode FEEDBACK_NOT_FOUND_FOR_NON_EXISTENT_PURCHASE = new ErrorCode("03", Collections.singletonList(getMessage("FEEDBACK_NOT_FOUND_FOR_NON_EXISTENT_PURCHASE", FeedbackErrorMessages.FEEDBACK_NOT_FOUND_FOR_NON_EXISTENT_PURCHASE)), 500);
}
