package com.kmicro.order.exception;

import org.slf4j.helpers.MessageFormatter;

public class CartException extends RuntimeException{
    public CartException(String msg){
        super(msg);
    }

    public CartException(String message, Object... args) {
        // This performs the {} substitution
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }
}
