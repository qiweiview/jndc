package com.view.jndc.server.enum_value;

/**
 * 枚举了一些常用API操作码
 * Created on 2019/4/19.
 */
public enum ResultCode {
    SUCCESS(0, "操作成功"),

    FAILED(-1, "操作失败"),

    ;


    private Integer code;
    private String message;


    private ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
