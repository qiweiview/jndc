package com.view.jndc.manage.enums;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public enum JNDCServerStatusEnum {


    PAUSE("暂停", "pause"),

    LISTEN("监听", "listen"),

    PROCESSING("处理中", "processing"),
    ;


    public final String label;

    public final String value;


    JNDCServerStatusEnum(String label, String value) {
        this.label = label;
        this.value = value;
    }

}
