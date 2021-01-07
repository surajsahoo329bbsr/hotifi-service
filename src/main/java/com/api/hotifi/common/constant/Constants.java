package com.api.hotifi.common.constant;

public class Constants {

    //TODO add hotifi upi and commission
    public static final String APP_PACKAGE_NAME = "com.api.hotifi";
    public static final String HOTIFI_UPI_ID = "hotifi@ybl";
    public static final int COMMISSION_PERCENTAGE = 20;

    //TODO add Html files and it's paths and emails
    public static final String EMAIL_HOST = "";
    public static final int EMAIL_PORT = 8080;
    public static final String FROM_EMAIL = "";
    public static final String FROM_EMAIL_PASSWORD = "";
    public static final String EMAIL_OTP_HTML_PATH = "D:\\PrivateProjects\\krishled-service-suraj\\src\\main\\java\\com\\online\\platform\\html\\email\\user_verification.html";
    public static final String EMAIL_WELCOME_HTML_PATH = "D:\\PrivateProjects\\krishled-service-suraj\\src\\main\\java\\com\\online\\platform\\html\\email\\user_verified.html";
    public static final String EMAIL_GOODBYE_HTML_PATH = "D:\\PrivateProjects\\krishled-service-suraj\\src\\main\\java\\com\\online\\platform\\html\\email\\user_verified.html";

    //Wifi password
    public static final String WIFI_PASSWORD_SECRET_KEY = "23afDeg@34$SWk10"; //128 Bit Key
    public static final String WIFI_PASSWORD_PATTERN = "(?=^.{20}$)(?=(.*\\d){4,6})(?=(.*[A-Z]){4,6})(?=(.*[a-z]){4,6})(?=(.*[!@#$%^&*?]){6})(?!.*[\\s])^.*";
    public static final String WIFI_PASSWORD_ENCRYPTION_ALGORITHM = "AES";

    //Swagger tags
    public static final String USER_TAG = "user";
    public static final String SPEED_TEST_TAG = "speed-test";
    public static final String USER_STATUS_TAG = "user-status";
    public static final String DEVICE_TAG = "device";
    public static final String SESSION_TAG = "session";
    public static final String PURCHASE_TAG = "purchase";
    public static final String SELLER_PAYMENT_TAG = "seller-payment";
    public static final String SELLER_RECEIPT_TAG = "seller-receipt";
    public static final String STATS_TAG = "stats";
    public static final String FEEDBACK_TAG = "feedback";
    public static final String AUTHENTICAION_TAG = "authenticate";

    //Patterns
    public static final String VALID_URL_PATTERN = "((http|https)://)(www.)?"
            + "[a-zA-Z0-9@:%._\\+~#?&//=]"
            + "{2,256}\\.[a-z]"
            + "{2,6}\\b([-a-zA-Z0-9@:%"
            + "._\\+~#?&//=]*)";
    public static final String VALID_OTP_PATTERN = "^[0-9]{4,6}$";
    public static final String VALID_PHONE_PATTERN = "^[0-9]{10,15}$";
    public static final String VALID_USERNAME_PATTERN = "^(?=[a-zA-Z0-9._]{6,30}$)(?!.*[_.]{2})[^_.].*[^_.]$";
    public static final String VALID_COUNTRY_CODE_PATTERN = "^[0-9]{1,5}$";
    public static final String VALID_UPI_ID_PATTERN = "[a-zA-Z0-9.\\\\-_]{3,256}@[a-zA-Z]{3,64}";
    public static final String VALID_ROLE_PATTERN = "(BUYER|SELLER|DELETE)";

    //Range
    public static final String MINIMUM_UPLOAD_SPEED = "0.25";
    public static final String MINIMUM_DOWNLOAD_SPEED = "1.00";
    public static final String MINIMUM_DATA_USED_MB = "0.00";
    public static final int MINIMUM_SELLING_DATA = 100; //100 MB
    public static final int MAXIMUM_SELLING_DATA = 204800; //200 GB
    public static final int MINIMUM_SELLING_DATA_PRICE = 1;
    public static final int MAXIMUM_SELLING_DATA_PRICE = 40;
    public static final int MINIMUM_DATA_THRESHOLD_MB = 5;
    public static final int MINIMUM_WITHDRAWAL_AMOUNT = 20;
    public static final int MAXIMUM_WITHDRAWAL_AMOUNT = 10000;
    public static final int MAXIMUM_REFUND_WITHDRAWAL_LIMIT = 1000;
    public static final int MAXIMUM_SELLER_AMOUNT_EARNED = 20000;
    public static final int MAXIMUM_EMAIL_OTP_MINUTES = 10;
    public static final int MINIMUM_WITHDRAWAL_DUE_DAYS = 30;
    public static final int MINIMUM_AMOUNT_PAID_INR = 1;
    public static final int MAXIMUM_AMOUNT_PAID_INR = 8000;
    public static final int MINIMUM_FREEZE_PERIOD_HOURS = 24;
    public static final int MINIMUM_WARNINGS_TO_FREEZE = 10;
    public static final int MINIMUM_WARNINGS_TO_BAN = 15;

}
