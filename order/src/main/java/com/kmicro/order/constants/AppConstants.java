package com.kmicro.order.constants;

import java.time.ZoneId;

public final class AppConstants {


    private AppConstants() {}

    public static final String ASIA_TIME_ZONE = "Asia/Kolkata";
    public static final ZoneId ASIA_ZONE_ID = ZoneId.of(AppConstants.ASIA_TIME_ZONE);
    public static final String HR_12_FORMAT = "yyyy-MM-dd hh:mm:ss.SS";
    public static final String HR_24_FORMAT = "yyyy-MM-dd HH:mm:ss.SS";

    public static final String PAYMENT_SERVICE_URL = "http://localhost:8095/api/payment";
    public static final String USER_CART_URL = "http://localhost:8090/api/cart";

    public static final String REDIS_ORDER_KEY_PREFIX = "order:";
    public static final String CACHE_PREFIX_ORDER = "cacheOrder";
    public static final String CACHE_PREFIX_USER = "cacheUser";
    public static final String REDIS_CART_KEY_PREFIX = "cart:";

    public static final String TEMP_TRANSACTION_ID = "tempTrnx_01";
    public static final String TEMP_TRACKING_ID = "track_01";
    public static final String TRACKING_URL = "https://kmicro.com/track/";
    public static final String PRODUCT_URL = "https://kmicro.com/product/";

}
