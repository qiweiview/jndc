package com.view.jndc.server.config;


import com.view.jndc.server.model.EncryptedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public EncryptedResponse handleException(Exception e) {
        if (e instanceof RuntimeException) {
            String msg = "【业务异常】" + e.getMessage();
            return EncryptedResponse.failed(msg);
        }
        String msg = "【系统异常】" + e.getMessage();
        log.error("【系统异常】", e);
        return EncryptedResponse.failed(msg);
    }


}
