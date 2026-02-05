package com.kmicro.order.constants;

public final class KafkaConstants {


    public static final String USER_KEY_PREFIX = "PFX_USR_";
    public static final String NOTIFICATION_KEY_PREFIX = "PFX_NTF_";

    //-----  KAFKA GROUP ID
    public static final String USERS_GROUP_ID = "user-service-group";
    public static final String NOTIFICATION_GROUP_ID = "notification-service-group";

    //-----    EVENT TYPE -- HANDLED OR GENERATED EVENTS
    public static final String ET_VERIFY_EMAIL = "emailVerification";
    public static final String ET_WELCOME_USER = "welcomeNewUser";
    public static final String ET_OTP_VERIFICATION = "optVerification";
    public static final String ET_PASSWORD_RESET = "resetPassword";
    public static final String ET_SHARE_USER_DETAILS = "shareUserDetails";
    public static final String ET_REQUEST_USER_DETAILS = "requestUserDetails";
    public static final String ET_ORDER_CREATED = "orderCreated";
    public static final String ET_ORDER_CONFIRMERD = "orderConfirmed";
    public static final String ET_ORDER_STATUS_UPDATED = "orderStatusUpdated";

    //-----    SYSTEM NAMES -- CAN BE TARGET OR SOURCE
    public static final String SYSTEM_NOTIFICATION = "notification-service";
    public static final String SYSTEM_ORDER ="order-cart-service";
    public static final String SYSTEM_USER = "user-service";
    public static final String SYSTEM_PAYMENT = "payment-service";

    //-----    TOPICS NAMES
    public static final String ORDER_TOPIC ="t-order-events";
    public static final String PAYMENT_TOPIC = "t-payment-events";
    public static final String USERS_TOPIC = "t-user-events";

    //---  INCOMING DATA KEYS
    public static final String DT_UNAME = "name";
    public static final String DT_USER_DATA = "userData";
    public static final String DT_LOGIN_NAME = "login_name";
    public static final String DT_USER_ID = "user_id";
    public static final String DT_SEND_TO = "sendto";
    public static final String DT_SUBJECT = "subject";
    public static final String DT_EMAIL = "email";
    public static final String DT_BODY= "body";
    public static final String DT_CITY= "city";
    public static final String DT_CONTACT= "contact";
    public static final String DT_SHIP_ADDRS= "shipping_address";
    public static final String DT_ADDRS_ID= "address_id";
    public static final String DT_ADDRESS_NODE= "address";
    public static final String DT_ZCODE= "zip_code";
    public static final String DT_COUNTRY= "country";
    public static final String DT_STATE= "state";
}//EC
