package com.view.enums;

import lombok.Getter;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-09-10 9:31
 * @description: 日志
 */
@Getter
public enum RequestResultEnum {

    /**
     * 成功
     */
    SUCCESS(0,"成功"),

    /**
     * 失败
     */
    FAILURE(1,"失败");

    private final int code;
    private final String message;

    RequestResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "OperationResult{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
