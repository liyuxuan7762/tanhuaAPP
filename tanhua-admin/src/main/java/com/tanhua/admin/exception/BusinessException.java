package com.tanhua.admin.exception;

//自定义异常
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BusinessException(String message) {
        super(message);
    }
	
}