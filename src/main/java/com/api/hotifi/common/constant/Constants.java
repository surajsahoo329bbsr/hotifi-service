package com.api.hotifi.common.constant;

public class Constants {

    //Hotifi Configuration
    public static final String APP_VERSION = "v1";
    public static final String APP_NAME = "Hotifi";
    //public static final String APP_PACKAGE_NAME = "com.api.hotifi";
    public static final String HOTIFI_BANK_ACCOUNT = "hotifi_bank";
    public static final int COMMISSION_PERCENTAGE = 20;
    public static final String SIGNING_KEY = "hotifi-api-connect";
    public static final String HOTIFI_OAUTH2_CLIENT_ID = "hotifi-android-user";
    public static final String HOTIFI_OAUTH2_CLIENT_SECRET = "W@#ifi#45feBah0b";

    //Razorpay Configuration
    public static final String RAZORPAY_CLIENT_ID = "";
    public static final String RAZORPAY_CLIENT_SECRET = "";

    public static final String GOOGLE_FIREBASE_PROJECT_NAME = "hotifi-app";
    public static final String GOOGLE_FIREBASE_JSON_FILE_CONFIGURATION_PATH = "hotifi_app_firebase_service_account.json";

    //Facebook Configuration
    public static final String FACEBOOK_APP_ID = "194688711814079";
    public static final String FACEBOOK_APP_SECRET = "691045cba5118ebd9dec29a7c85510de";
    public static final String FACEBOOK_GRAPH_API_URL = "https://graph.facebook.com";

    //1 GB in MB
    public static final int UNIT_GB_VALUE_IN_MB = 1024;
    public static final int UNIT_INR_IN_PAISE = 100;

    public static final String EMAIL_HOST = "smtp.gmail.com";
    public static final int EMAIL_PORT = 587;
    public static final String FROM_EMAIL = "hotifi.app@gmail.com";
    public static final String FROM_EMAIL_PASSWORD = "";
    public static final String EMAIL_OTP_HTML_PATH = "static/email_otp.html";
    public static final String EMAIL_WELCOME_HTML_PATH = "static/welcome.html";
    public static final String EMAIL_ACCOUNT_DELETED_HTML_PATH = "static/account_deleted.html";
    public static final String EMAIL_ACCOUNT_FREEZED_HTML_PATH = "static/account_freezed.html";
    public static final String EMAIL_BUYER_BANNED_HTML_PATH = "static/buyer_banned.html";
    public static final String EMAIL_LINKED_ACCOUNT_FAILED_PATH = "static/linked_account_failed.html";
    public static final String EMAIL_LINKED_ACCOUNT_SUCCESS_PATH = "static/linked_account_success.html";

    //Wifi password
    public static final String AES_PASSWORD_SECRET_KEY = "23afDeg@34$SWk10"; //128 Bit Key
    public static final String WIFI_PASSWORD_PATTERN = "(?=^.{20}$)(?=(.*\\d){4,6})(?=(.*[A-Z]){4,6})(?=(.*[a-z]){4,6})(?=(.*[!@#$%^&*?]){6})(?!.*[\\s])^.*";
    public static final String WIFI_PASSWORD_ENCRYPTION_ALGORITHM = "AES";

    //Swagger tags
    public static final String USER_TAG = "user";
    public static final String SPEED_TEST_TAG = "speed-test";
    public static final String USER_STATUS_TAG = "user-status";
    public static final String DEVICE_TAG = "device";
    public static final String SESSION_TAG = "session";
    public static final String PURCHASE_TAG = "purchase";
    public static final String BANK_ACCOUNT = "bank-account";
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
    public static final String VALID_ROLE_PATTERN = "(BUYER|SELLER|DELETE)";

    //Range
    public static final String MINIMUM_UPLOAD_SPEED = "0.25";
    public static final String MINIMUM_DOWNLOAD_SPEED = "1.00";
    public static final String MINIMUM_DATA_USED_MB = "0.00";
    public static final int MINIMUM_SELLING_DATA_MB = 100; //100 MB
    public static final int MAXIMUM_SELLING_DATA_MB = 204800; //200 GB
    public static final int MINIMUM_SELLING_DATA_PRICE_PER_GB = 8;
    public static final int MAXIMUM_SELLING_DATA_PRICE_PER_GB = 40;
    public static final int MINIMUM_DATA_THRESHOLD_MB = 5;
    public static final int MINIMUM_WITHDRAWAL_AMOUNT = 5;
    public static final int MAXIMUM_WITHDRAWAL_AMOUNT = 10000;
    public static final int MAXIMUM_REFUND_WITHDRAWAL_LIMIT = 1000;
    public static final int MAXIMUM_SELLER_AMOUNT_EARNED = 20000;
    public static final int MAXIMUM_EMAIL_OTP_MINUTES = 10;
    public static final int MINIMUM_SELLER_WITHDRAWAL_DUE_DAYS = 30;
    public static final int MINIMUM_AMOUNT_INR = 1;
    //public static final int MAXIMUM_AMOUNT_PAID_INR = 8000;
    public static final int MINIMUM_FREEZE_PERIOD_HOURS = 24;
    public static final int MINIMUM_WARNINGS_TO_FREEZE = 10;
    public static final int MINIMUM_WARNINGS_TO_BAN = 15;
    public static final int MAXIMUM_BUYER_REFUND_DUE_HOURS = 72;

    //Status codes
    public static final int BUYER_PAYMENT_START_VALUE_CODE = 0;
    public static final int SELLER_PAYMENT_START_VALUE_CODE = 1000;
    public static final int PAYMENT_METHOD_START_VALUE_CODE = 100;
    public static final int CLOUD_CLIENT_START_VALUE_CODE = 100;
    public static final int SOCIAL_START_VALUE_CODE = 100;
    public static final int RAZORPAY_PAYMENT_STATUS_START_VALUE_CODE = 0;
    public static final int RAZORPAY_REFUND_STATUS_START_VALUE_CODE = 0;
    public static final int RAZORPAY_TRANSFER_STATUS_START_VALUE_CODE = 1;
    public static final int PAYMENT_GATEWAY_START_VALUE_CODE = 1;
    public static final int ACCOUNT_TYPE_START_VALUE_CODE = 1;
    public static final int BANK_ACCOUNT_TYPE_START_VALUE_CODE = 1;

}
