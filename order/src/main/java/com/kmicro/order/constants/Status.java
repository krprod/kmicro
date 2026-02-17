package com.kmicro.order.constants;

public enum Status {

    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    READY_TO_SHIP("READY_TO_SHIP"),
    SHIPPED("SHIPPED"),
    DELIVERED("DELIVERED"),
    FAILED("FAILED"),
    PAYMENT_FAILED("PAYMENT_FAILED"),
    PAYMENT_RETRY("PAYMENT_RETRY"),
    PAYMENT_PENDING("PAYMENT_PENDING"),
    PAYMENT_SUCCESS("PAYMENT_SUCCESS"),
    COMPLETED("COMPLETED"),
    CONFIRMED("CONFIRMED"),
    FAILED_PLATFORM("FAILED_PLATFORM"),
    CANCELLED("CANCELLED");

    private String name;
    private Status(String name) {
        this.name = name;
    }
}
