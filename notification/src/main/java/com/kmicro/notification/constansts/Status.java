package com.kmicro.notification.constansts;

public enum Status {

    PENDING("PENDING"),
    PROCESSING("PROCESSING"),
    READY("READY"),
    SENT("SENT"),
    WAITING_ON_USER_SERVICE("WAITING_ON_USER_SERVICE"),
    WAITING_ON_PAYMENT_SERVICE("WAITING_ON_PAYMENT_SERVICE"),
    PERMANENT_FAILURE("PERMANENT_FAILURE"),
    DELIVERED("DELIVERED"),
    FAILED("FAILED");

    private String name;
    private Status(String name) {
        this.name = name;
    }
}
