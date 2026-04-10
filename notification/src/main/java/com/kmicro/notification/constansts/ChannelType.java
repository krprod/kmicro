package com.kmicro.notification.constansts;

public enum ChannelType {
    EMAIL("EMAIL"),
    PUSH("PUSH"),
    IN_APP("IN_APP"),
    SMS("SMS");

    private String name;
    private ChannelType(String name) {
        this.name = name;
    }
}
