package com.kmicro.product.exception;

import org.slf4j.helpers.MessageFormatter;

public class DataNotExistException extends RuntimeException{
    public DataNotExistException(String msg){
        super( msg);
    }

    public DataNotExistException(String message, Object... args) {
        // This performs the {} substitution
        super(MessageFormatter.arrayFormat(message, args).getMessage());
    }
}
