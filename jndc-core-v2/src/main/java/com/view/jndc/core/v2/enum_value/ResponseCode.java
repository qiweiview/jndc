package com.view.jndc.core.v2.enum_value;

public enum ResponseCode {
    SUCCESS("成功", 0),

    FAIL("失败", 500),
    ;

    public String name;
    public Integer value;


    ResponseCode(String name, Integer value) {
        this.name = name;
        this.value = value;
    }
}
