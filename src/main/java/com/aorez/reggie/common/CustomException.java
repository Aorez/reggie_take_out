package com.aorez.reggie.common;

//自定义异常，业务出现异常即可抛出，携带异常信息
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
