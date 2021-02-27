package com.api.hotifi.identity.errors;

import com.api.hotifi.common.exception.errors.ErrorCode;
import com.api.hotifi.common.exception.errors.ErrorCodes;

import java.util.Collections;

public class UserStatusErrorCodes extends ErrorCodes {
    //Internal Error Codes
    public static ErrorCode UNEXPECTED_USER_STATUS_ERROR = new ErrorCode("00", Collections.singletonList(getMessage("UNEXPECTED_USER_STATUS_ERROR", UserStatusErrorMessages.UNEXPECTED_USER_STATUS_ERROR)), 500);
    public static ErrorCode NEITHER_WARNING_NOR_DELETE_REASON = new ErrorCode("01", Collections.singletonList(getMessage("NEITHER_WARNING_NOR_DELETE_REASON", UserStatusErrorMessages.NEITHER_WARNING_NOR_DELETE_REASON)), 500);
    public static ErrorCode USER_FREEZE_REASON_ABSENT = new ErrorCode("02", Collections.singletonList(getMessage("USER_FREEZE_REASON_ABSENT", UserStatusErrorMessages.USER_FREEZE_REASON_ABSENT)), 500);
    public static ErrorCode USER_BAN_REASON_ABSENT = new ErrorCode("03", Collections.singletonList(getMessage("USER_BAN_REASON_ABSENT", UserStatusErrorMessages.USER_BAN_REASON_ABSENT)), 500);
    public static ErrorCode USER_FREEZE_PERIOD_ACTIVE = new ErrorCode("04", Collections.singletonList(getMessage("USER_FREEZE_PERIOD_ACTIVE", UserStatusErrorMessages.USER_FREEZE_PERIOD_ACTIVE)), 500);
}
