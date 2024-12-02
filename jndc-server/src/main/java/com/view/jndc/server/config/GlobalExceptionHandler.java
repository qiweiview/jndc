package com.view.jndc.server.config;


import com.view.jndc.server.config.exception.InvalidTokenException;
import com.view.jndc.server.model.EncryptedResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public EncryptedResponse handleException(Exception e) {
        log.error("【异常】", e);
        if (e instanceof InvalidTokenException) {
            String msg = "【权限校验失败】" + e.getMessage();
            EncryptedResponse failed = EncryptedResponse.failed(msg);
            failed.setCode(403);
            return failed;
        }
        else if (e instanceof RuntimeException) {
            String msg = "【业务异常】" + e.getMessage();
            return EncryptedResponse.failed(msg);
        }
        String msg = "【系统异常】" + e.getMessage();
        return EncryptedResponse.failed(msg);
    }


}
