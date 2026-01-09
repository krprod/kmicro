package com.kmicro.order.constants;

public enum OrderStatus {

    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    READY_TO_SHIP("READY_TO_SHIP"),
    SHIPPED("SHIPPED"),
    DELIVERED("DELIVERED"),
    FAILED("FAILED"),
    FAILED_PAYMENT("FAILED_PAYMENT"),
    COMPLETED("COMPLETED"),
    CONFIRMED("CONFIRMED"),
    FAILED_PLATFORM("FAILED_PLATFORM"),
    CANCELLED("CANCELLED");

    private String name;
    private OrderStatus(String name) {
        this.name = name;
    }
}
