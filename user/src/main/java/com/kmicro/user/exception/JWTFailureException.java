package com.kmicro.user.exception;

public class JWTFailureException extends RuntimeException{
    public JWTFailureException(String msg){
        super( msg);
    }
}
