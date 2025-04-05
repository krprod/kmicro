package com.kmicro.order.dtos;

public enum OrderStatusEnum {

    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    READYTOSHIP("READYTOSHIP"),
    SHIPPED("SHIPPED"),
    DELIVERED("DELIVERED"),
    FAILED("FAILED"),
    FAILEDPAYMENT("FAILEDPAYMENT"),
    COMPLETED("COMPLETED"),
    CONFIRMED("CONFIRMED"),
    FAILEDPLATEFORM("FAILEDPLATEFORM"),
    CANCELLED("CANCELLED");

    private String name;
    private OrderStatusEnum(String name) {
        this.name = name;
    }
}
