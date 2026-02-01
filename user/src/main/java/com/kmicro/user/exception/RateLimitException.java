package com.kmicro.user.exception;

public class RateLimitException extends RuntimeException{
    public RateLimitException(String msg){
        super(msg);
    }
}
