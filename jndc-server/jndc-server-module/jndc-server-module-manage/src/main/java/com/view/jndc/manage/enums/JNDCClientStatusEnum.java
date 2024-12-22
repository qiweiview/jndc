package com.view.jndc.manage.enums;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public enum JNDCClientStatusEnum {


    PAUSE("暂停", "pause"),

    CONNECT("连接", "connect"),

    PROCESSING("处理中", "processing"),
    ;


    public final String label;

    public final String value;


    JNDCClientStatusEnum(String label, String value) {
        this.label = label;
        this.value = value;
    }

}
