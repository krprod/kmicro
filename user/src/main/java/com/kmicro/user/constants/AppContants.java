package com.kmicro.user.constants;

import java.util.Map;

public final class AppContants {

    public static final String JWT_SECRET_KEY = "JWT_SECRET";
    public static final String JWT_SECRET_DEFAULT_VALUE = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
    public static final String JWT_HEADER = "Authorization";

    public static final String ORDER_TOPIC = "t-order-events";
    public static final String PAYMENT_TOPIC = "t-payment-events";
    public static final String USERS_TOPIC = "t-user-events";
    public static final String USERS_GROUP_ID = "user-service-group";
    public static final String KAFKA_KEY_PREFIX = "PFX_USR_";

    public static final Map<String, String> EVENT_TYPES = Map.of(
            "SHARE_USER_DETAILS","userDetailShared",
            "ORDER_CONFIRM","orderConfirm",
            "PAYMENT_REQ","newPaymentRequest"
    );

    public static final Map<String, String> SOURCE_SYSTEMS = Map.of(
            "NOTIFICATION","notification-service",
            "ORDER","order-cart-service",
            "USER","user-service",
            "PAYMENT","payment-service"
    );
}