package com.api.hotifi.common.constants.configurations;

public class BusinessConfigurations {

    //1 GB in MB
    public static final String HOTIFI_BANK_ACCOUNT = "Hotifi";
    public static final int COMMISSION_PERCENTAGE = 20;
    public static final int UNIT_GB_VALUE_IN_MB = 1024;
    public static final int UNIT_INR_IN_PAISE = 100;
    public static final String CURRENCY_INR = "INR";

    //Wifi password
    public static final String WIFI_PASSWORD_PATTERN = "(?=^.{20}$)(?=(.*\\d){4,6})(?=(.*[A-Z]){4,6})(?=(.*[a-z]){4,6})(?=(.*[!@#$%^&*?]){6})(?!.*[\\s])^.*";
    public static final String WIFI_PASSWORD_ENCRYPTION_ALGORITHM = "AES";

    //Patterns
    public static final String VALID_URL_PATTERN = "((http|https)://)(www.)?"
            + "[a-zA-Z0-9@:%._\\+~#?&//=]"
            + "{2,256}\\.[a-z]"
            + "{2,6}\\b([-a-zA-Z0-9@:%"
            + "._\\+~#?&//=]*)";
    public static final String VALID_UPI_PATTERN = "^[\\w.-]+@[\\w.-]+$";
    public static final String VALID_OTP_PATTERN = "^[0-9]{4,7}$";
    public static final String VALID_PHONE_PATTERN = "^[0-9]{10,15}$";
    public static final String VALID_USERNAME_PATTERN = "^(?=[a-zA-Z0-9._]{6,30}$)(?!.*[_.]{2})[^_.].*[^_.]$";
    public static final String VALID_COUNTRY_CODE_PATTERN = "^[0-9]{1,5}$";
    public static final String VALID_ROLE_PATTERN = "(BUYER|SELLER|DELETE)";
    public static final String DATABASE_EPOCH_TO_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

    //Range
    public static final String MINIMUM_DOWNLOAD_SPEED_MEGABYTES = "0.5"; // 4 Mbps (bits per second)
    public static final String MINIMUM_UPLOAD_SPEED_MEGABYTES = "0.125"; // 1 Mbps (bits per second)
    public static final double MINIMUM_WIFI_DOWNLOAD_SPEED_MEGABYTES = 0.625D; // 5 Mbps (bits per second)
    public static final double MINIMUM_WIFI_UPLOAD_SPEED_MEGABYTES = 0.156D; // 1.248 Mbps (bits per second)
    public static final String MINIMUM_DATA_USED_MB = "0.00";
    public static final int MINIMUM_SELLING_DATA_MB = 100; //100 MB
    public static final int MINIMUM_BUYING_DATA_MB = 10; //100 MB
    public static final int MAXIMUM_SELLING_DATA_MB = 204800; //200 GB
    public static final int MINIMUM_SELLING_DATA_PRICE_PER_GB = 7;
    public static final int MAXIMUM_SELLING_DATA_PRICE_PER_GB = 21;
    public static final int MINIMUM_DATA_THRESHOLD_MB = 5;
    public static final int MINIMUM_WITHDRAWAL_AMOUNT_BEFORE_MATURITY = 20; //If this happens within 20 days or later
    public static final int MAXIMUM_WITHDRAWAL_AMOUNT = 10000;
    public static final int MAXIMUM_SELLER_AMOUNT_EARNED = 20000;
    public static final int MAXIMUM_EMAIL_OTP_MINUTES = 10;
    public static final int MINIMUM_SELLER_WITHDRAWAL_HOURS = 24 * 20; //24 hours - 20 days
    public static final int MINIMUM_WITHDRAWAL_AMOUNT = 1; //If this happens after 20 days
    public static final int MINIMUM_PURCHASE_AMOUNT = 0;
    public static final int MINIMUM_PRICE_BUDGET_PER_OFFER = 1;
    public static final int MINIMUM_FREEZE_PERIOD_HOURS = 24;
    public static final int SELLER_SESSION_CLOSE_WAIT_TIME_MINUTES = 1;
    public static final int MAXIMUM_BUYER_REFUND_DUE_HOURS = 168; // 7 days refund policy
    public static final int MAXIMUM_TOLERABLE_ABNORMAL_ACTIVITY_SECONDS = 60; // 7 days refund policy

    //Status codes
    public static final int BUYER_PAYMENT_START_VALUE_CODE = 0;
    public static final int SELLER_PAYMENT_START_VALUE_CODE = 1000;
    public static final int PAYMENT_METHOD_START_VALUE_CODE = 100;
    public static final int CLOUD_CLIENT_START_VALUE_CODE = 100;
    public static final int SOCIAL_START_VALUE_CODE = 100;
    public static final int RAZORPAY_ORDER_STATUS_START_VALUE_CODE = 100;
    public static final int RAZORPAY_PAYMENT_STATUS_START_VALUE_CODE = 0;
    public static final int RAZORPAY_REFUND_STATUS_START_VALUE_CODE = 0;
    public static final int PAYMENT_GATEWAY_START_VALUE_CODE = 1;
    public static final int ACCOUNT_TYPE_START_VALUE_CODE = 1;
    public static final int NETWORK_PROVIDER_START_VALUE_CODE = 1;
    public static final int BANK_ACCOUNT_TYPE_START_VALUE_CODE = 1;

    public static final String CAPITAL_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final int REFERRAL_CODE_LENGTH = 10;
    public static final String REFERRAL_CODE_PREFIX = "REF";

}
