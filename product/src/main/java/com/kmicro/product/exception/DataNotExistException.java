package com.kmicro.product.exception;

public class DataNotExistException extends RuntimeException{
    public DataNotExistException(String msg){
        super( msg);
    }
}
