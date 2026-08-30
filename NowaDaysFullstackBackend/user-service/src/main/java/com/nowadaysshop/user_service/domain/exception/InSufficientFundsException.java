package com.nowadaysshop.user_service.domain.exception;

public class InSufficientFundsException extends RuntimeException{
    public InSufficientFundsException(String message){
        super(message);
    }
}
