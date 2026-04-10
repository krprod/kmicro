package com.kmicro.user.exception;

public class AccountDeactivated extends RuntimeException{
    public AccountDeactivated(String msg){
        super(msg);
    }
}
