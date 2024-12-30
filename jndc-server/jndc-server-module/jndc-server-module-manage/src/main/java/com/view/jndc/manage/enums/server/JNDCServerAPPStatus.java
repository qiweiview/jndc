package com.view.jndc.manage.enums.server;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public enum JNDCServerAPPStatus {


    LISTEN("监听", "LISTEN"),

    STOP("停止", "STOP"),

    ;


    public final String label;

    public final String value;


    JNDCServerAPPStatus(String label, String value) {
        this.label = label;
        this.value = value;
    }

}
