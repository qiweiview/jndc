package com.view.jndc.manage.enums;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public enum JNDCClientServiceStatusEnum {


    REGISTER("监听", "register"),

    UN_REGISTER("暂停", "unregister"),

    ;


    public final String label;

    public final String value;


    JNDCClientServiceStatusEnum(String label, String value) {
        this.label = label;
        this.value = value;
    }

}
