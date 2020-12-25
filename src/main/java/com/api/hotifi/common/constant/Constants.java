package com.api.hotifi.common.constant;

public class Constants {
    public static final String APP_PACKAGE_NAME = "com.api.hotifi";
    public static final String HOTIFI_UPI_ID = "hotifi@ybl"; //TODO
    public static final String WIFI_PASSWORD_SECRET_KEY = "23afDeg@34$SWk10"; //128 Bit Key
    public static final String WIFI_PASSWORD_ENCRYPTION_ALGORITHM = "AES";
    public static final String USER_TAG = "user";
    public static final String SPEED_TEST_TAG = "speed-test";
    public static final String USER_STATUS_TAG = "user-status";
    public static final String DEVICE_TAG = "device";
    public static final String SESSION_TAG = "session";
    public static final String PURCHASE_TAG = "purchase";
    public static final String AUTHENTICAION_TAG = "authenticate";
    public static final String VALID_OTP_PATTERN = "^[0-9]{4,6}$";
    public static final String VALID_PHONE_PATTERN = "^[0-9]{10,15}$";
    public static final String VALID_USERNAME_PATTERN = "^(?=[a-zA-Z0-9._]{6,30}$)(?!.*[_.]{2})[^_.].*[^_.]$";
    public static final String VALID_COUNTRY_CODE_PATTERN = "^[0-9]{1,5}$";
    public static final String VALID_UPI_ID_PATTERN = "[a-zA-Z0-9.\\\\-_]{3,256}@[a-zA-Z]{3,64}";
    public static final String VALID_ROLE_PATTERN = "(BUYER|SELLER|DELETE)";
    public static final String MINIMUM_UPLOAD_SPEED = "0.25";
    public static final String MINIMUM_DOWNLOAD_SPEED = "1.00";
    public static final int MINIMUM_SELLING_DATA = 100;
    public static final int MINIMUM_SELLING_DATA_PRICE = 1;
    public static final int MAXIMUM_SELLING_DATA_PRICE = 40;
    public static final int MINIMUM_DATA_THRESHOLD_MB = 5;
    public static final String MINIMUM_AMOUNT_PAID_INR = "1.00";
    public static final String MAXIMUM_AMOUNT_PAID_INR = "100000.00";
    public static final String MINIMUM_DATA_USED_MB = "0.00";
}
