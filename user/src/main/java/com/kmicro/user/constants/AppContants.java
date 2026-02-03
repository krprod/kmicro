package com.kmicro.user.constants;

import java.time.ZoneId;

public final class AppContants {

    public static final String ASIA_TIME_ZONE = "Asia/Kolkata";
    public static final ZoneId ASIA_ZONE_ID = ZoneId.of(AppContants.ASIA_TIME_ZONE);
    public static final String HR_12_FORMAT = "yyyy-MM-dd hh:mm:ss.SS";
    public static final String HR_24_FORMAT = "yyyy-MM-dd HH:mm:ss.SS";

    public static final String JWT_SECRET_KEY = "JWT_SECRET";
    public static final String JWT_SECRET_DEFAULT_VALUE = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
    public static final String JWT_HEADER = "Authorization";
    public static final Long EMAIL_VERIFY_TOKEN_DURATION = 5L;
    public static final Long RESEND_COOLDOWN_MINUTES = 5L;

    public static final String REDIS_VERIFY_KEY_PREFIX = "verify:";
    public static final String VERIFICATION_LINK_URI = "http://localhost:8085/api/auth/verify?token=";

    public static final String SERVICE_REDIS_KEY_PREFIX = "USR";
    public static final String CACHE_ADDRESS_KEY_PX = "address";
    public static final String CACHE_USER_KEY_PX = "user";


}//EC