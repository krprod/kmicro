package com.kmicro.order.constants;

public enum PaymentMethod {
        CREDIT_CARD("CREDIT_CARD"),
        PAYPAL("PAYPAL"),
        BANK_TRANSFER("BANK_TRANSFER"),
        ONLINE("ONLINE"),
        PAYTM("PAYTM"),
        PHONE_PE("PHONE_PE"),
        CASH_ON_DELIVERY("CASH_ON_DELIVERY");

    private String name;
    private PaymentMethod(String name) {
        this.name = name;
    }

}
