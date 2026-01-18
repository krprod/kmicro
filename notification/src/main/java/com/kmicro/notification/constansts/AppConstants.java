package com.kmicro.notification.constansts;

import java.time.ZoneId;
import java.util.Map;

public final class AppConstants {
    private AppConstants() {}

    public static final String ASIA_TIME_ZONE = "Asia/Kolkata";
    public static final ZoneId ASIA_ZONE_ID = ZoneId.of(AppConstants.ASIA_TIME_ZONE);
    public static final String HR_12_FORMAT = "yyyy-MM-dd hh:mm:ss.SS";
    public static final String HR_24_FORMAT = "yyyy-MM-dd HH:mm:ss.SS";

    public static final String PAYMENT_SERVICE_URL = "http://localhost:8095/api/payment";
    public static final String USER_CART_URL = "http://localhost:8090/api/cart";
    public static final String TOPIC = "order-events";
    public static final String REDIS_ORDER_KEY_PREFIX = "Order_";
    public static final String REDIS_CART_KEY_PREFIX = "CART_";

    public static final String TEMP_TRANSACTION_ID = "tranx_01";
    public static final String TEMP_TRACKING_ID = "track_01";

    public static final String NO_REPLY_MAIL  = System.getenv("NO_REPLY_MAIL_ADDRESS");

    public static final String ORDER_TOPIC = "t-order-events";
    public static final String PAYMENT_TOPIC = "t-payment-events";
    public static final String USERS_TOPIC = "t-user-events";
    public static final String KAFKA_KEY_PREFIX = "PFX_NTF_";

    public static final Map<String, String> EVENT_TYPES = Map.of(
            "USER_REQ","requestUserData"
    );

    public static final Map<String, String> SOURCE_SYSTEMS = Map.of(
            "NOTIFICATION","notification-service",
            "ORDER","order-cart-service",
            "USER","user-service",
            "PAYMENT","payment-service"
    );
}
