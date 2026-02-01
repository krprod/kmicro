package com.kmicro.user.constants;

import java.util.Map;

public final class KafkaConstants {


    public static final String KAFKA_KEY_PREFIX = "PFX_USR_";

    //-----  KAFKA GROUP ID
    public static final String USERS_GROUP_ID = "user-service-group";

    //-----    EVENT TYPE -- HANDLED OR GENERATED EVENTS
    public static final String ET_VERIFY_EMAIL = "emailVerification";
    public static final String ET_WELCOME_USER = "welcomeNewUser";
    public static final String ET_OTP_VERIFICATION = "optVerification";
    public static final String ET_PASSWORD_RESET = "resetPassword";
    public static final String ET_SHARE_USER_DETAILS = "shareUserDetails";
    public static final String ET_REQUEST_USER_DETAILS = "requestUserDetails";

    //-----    SYSTEM NAMES -- CAN BE TARGET OR SOURCE
    public static final String SYSTEM_NOTIFICATION = "notification-service";
    public static final String SYSTEM_ORDER ="order-cart-service";
    public static final String SYSTEM_USER = "user-service";
    public static final String SYSTEM_PAYMENT = "payment-service";

    //-----    TOPICS NAMES
    public static final String ORDER_TOPIC ="t-order-events";
    public static final String PAYMENT_TOPIC = "t-payment-events";
    public static final String USERS_TOPIC = "t-user-events";


    public static final Map<String, String> SOURCE_SYSTEMS = Map.of(
            "NOTIFICATION","notification-service",
            "ORDER","order-cart-service",
            "USER","user-service",
            "PAYMENT","payment-service"
    );
    public static final Map<String, String> TOPICS_LIST = Map.of(
            "USERS","t-user-events",
            "ORDERS","t-order-events",
            "PAYMENT","t-payment-events"
//            "NOTIFICATION","newPaymentRequest",
    );
    public static final Map<String, String> EVENT_TYPES = Map.of(
            "SHARE_USER_DETAILS","userDetailShared",
            "ORDER_CONFIRM","orderConfirm",
            "PAYMENT_REQ","newPaymentRequest",
            "USER_CREATED","newUserCreated"
    );

}//EC
