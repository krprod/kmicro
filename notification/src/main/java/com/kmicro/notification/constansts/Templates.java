package com.kmicro.notification.constansts;

public enum Templates {
    VERIFY_OTP ("/email/templates/VerifyEmailOTP.html"),

    //------- TEMPLATES
    DEFAULT_LAYOUT("/email/templates/default-layout.html"),
    MAIL_LAYOUT("/email/templates/mail-layout.html"),

    //------- FRAGMENTS
    FR_ORDER_CONFIRM("email/templates/fragment/order-confirmation :: email-body"),
    FR_ABANDON_CART("email/templates/fragment/abandoned-cart :: email-body"),
    FR_OPT_VERIFICATION("email/templates/fragment/otp-verification :: email-body"),
    FR_PAYMENT_FAIL("email/templates/fragment/payment-failure :: email-body"),
    FR_PAYMENT_SUCCESS("email/templates/fragment/payment-success :: email-body"),
    FR_SECURITY_ALERT("email/templates/fragment/security-alert :: email-body"),
    FR_SHIPPING_UPDATE("email/templates/fragment/shipping-update :: email-body"),
    FR_WELCOME("email/templates/fragment/welcome :: email-body"),
    FR_VERIFY_USER_EMAIL("email/templates/fragment/user-verification :: email-body");

    private String name;
    private Templates(String name) {
        this.name = name;
    }

    public String getName(){
        return  this.name;
    }

}
