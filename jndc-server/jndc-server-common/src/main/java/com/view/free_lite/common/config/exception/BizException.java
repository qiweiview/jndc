package com.view.free_lite.common.config.exception;

import lombok.extern.slf4j.Slf4j;

/**
 * 登录状态过期异常
 */
@Slf4j
public class BizException extends RuntimeException {
    public BizException(String message) {
        super(message);
    }
}
