package com.kmicro.order.dtos;

public enum OrderStatusEnum {

    PENDING("PENDING"),
    PROCESSING("PROCESSED"),
    READYTOSHIP("READY_TO_SHIP"),
    SHIPPED("SHIPPED"),
    DELIVERED("DELIVERED"),
    FAILED("FAILED"),
    FAILEDPAYMENT("FAILED_PAYMENT"),
    COMPLETED("COMPLETED"),
    CONFIRMED("CONFIRMED"),
    FAILEDPLATEFORM("FAILED_PLATEFORM"),
    CANCELLED("CANCELLED");

    private String name;
    private OrderStatusEnum(String name) {
        this.name = name;
    }
}
