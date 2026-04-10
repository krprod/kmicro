package com.kmicro.order.exception;

import org.slf4j.helpers.MessageFormatter;

public class OrderException extends RuntimeException{
    public OrderException(String msg){
        super(msg);
    }

    public OrderException(String message, Object... args) {
        // This performs the {} substitution
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }
}
