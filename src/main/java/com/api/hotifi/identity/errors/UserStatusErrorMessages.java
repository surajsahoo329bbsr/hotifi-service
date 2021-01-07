package com.api.hotifi.identity.errors;

public class UserStatusErrorMessages {
    public static final String NEITHER_WARNING_NOR_DELETE_REASON = "Request must have either warning or deletion reason";
    public static final String USER_WARNING_REASON_ABSENT = "Warning reason is absent";
    public static final String USER_DELETE_REASON_ABSENT = "Delete reason is absent";
    public static final String USER_FREEZE_REASON_ABSENT = "Freeze reason is absent";
    public static final String USER_BAN_REASON_ABSENT = "Ban reason is absent";
    public static final String USER_FREEZE_PERIOD_ACTIVE = "Freeze period is active";
    public static final String UNEXPECTED_USER_STATUS_ERROR = "Unexpected user status error";
}
