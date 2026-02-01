package com.kmicro.user.constants;

public final class AppContants {

    public static final String JWT_SECRET_KEY = "JWT_SECRET";
    public static final String JWT_SECRET_DEFAULT_VALUE = "jxgEQeXHuPq8VdbyYFNkANdudQ53YUn4";
    public static final String JWT_HEADER = "Authorization";
    public static final Long EMAIL_VERIFY_TOKEN_DURATION = 5L;
    public static final Long RESEND_COOLDOWN_MINUTES = 5L;
    public static final String REDIS_VERIFY_KEY_PREFIX = "verify:";
    public static final String VERIFICATION_LINK_URI = "http://localhost:8085/api/auth/verify?token=";



}//EC