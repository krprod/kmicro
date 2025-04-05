package com.kmicro.order.dtos;

public enum PaymentMethodEnum {
        CREDIT_CARD("CREDIT_CARD"),
        PAYPAL("PAYPAL"),
        BANK_TRANSFER("BANK_TRANSFER"),
        ONLINE("ONLINE"),
        PAYTM("PAYTM"),
        PHONEPE("PHONEPE"),
        CASH_ON_DELIVERY("CASH_ON_DELIVERY");

    private String name;
    private PaymentMethodEnum(String name) {
        this.name = name;
    }

}
