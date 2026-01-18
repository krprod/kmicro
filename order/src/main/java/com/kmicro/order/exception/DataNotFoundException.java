package com.kmicro.order.exception;

import org.slf4j.helpers.MessageFormatter;

public class DataNotFoundException extends RuntimeException{
    public DataNotFoundException(String msg){
        super(msg);
    }

    public DataNotFoundException(String message, Object... args) {
        // This performs the {} substitution
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }
}

