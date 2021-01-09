package com.api.hotifi.payment.error;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class FeedbackErrorCodes extends ErrorCodes {
    public static final ErrorCode UNEXPECTED_FEEDBACK_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_FEEDBACK_ERROR", FeedbackErrorMessages.UNEXPECTED_FEEDBACK_ERROR)), 500);
    public static final ErrorCode FEEDBACK_ALREADY_GIVEN = new ErrorCode("01", Collections.singletonList(getMessage("FEEDBACK_ALREADY_GIVEN", FeedbackErrorMessages.FEEDBACK_ALREADY_GIVEN)), 500);
    public static final ErrorCode NO_PURCHASE_NO_FEEDBACK = new ErrorCode("02", Collections.singletonList(getMessage("NO_PURCHASE_NO_FEEDBACK", FeedbackErrorMessages.NO_PURCHASE_NO_FEEDBACK)), 500);
    public static final ErrorCode NO_FEEDBACK_EXISTS_FOR_NO_PURCHASE = new ErrorCode("03", Collections.singletonList(getMessage("NO_FEEDBACK_EXISTS_FOR_NO_PURCHASE", FeedbackErrorMessages.NO_FEEDBACK_EXISTS_FOR_NO_PURCHASE)), 500);
}
