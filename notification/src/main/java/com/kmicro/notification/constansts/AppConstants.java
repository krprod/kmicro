package com.kmicro.notification.constansts;

import java.time.ZoneId;

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

    public static final String NO_REPLY_MAIL  ="no-reply.kmicro.com";
}
