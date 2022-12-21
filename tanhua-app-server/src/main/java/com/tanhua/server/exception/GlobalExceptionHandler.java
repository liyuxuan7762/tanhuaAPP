package com.tanhua.server.exception;

import com.tanhua.model.vo.ErrorResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 处理业务逻辑异常
     * @param businessException
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity handleBusinessException(BusinessException businessException) {
        businessException.printStackTrace();
        ErrorResult result = businessException.getResult();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity handleOtherExceptions(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResult.error());
    }

}
