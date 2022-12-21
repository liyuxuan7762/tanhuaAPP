package com.tanhua.server.exception;

import com.tanhua.model.vo.ErrorResult;
import lombok.Data;

/**
 * 这个自定义类用来封装业务异常
 */
@Data
public class BusinessException extends RuntimeException{
    private ErrorResult result;

    public BusinessException(ErrorResult errorResult) {
        super(errorResult.getErrMessage());
        this.result = errorResult;
    }


}
