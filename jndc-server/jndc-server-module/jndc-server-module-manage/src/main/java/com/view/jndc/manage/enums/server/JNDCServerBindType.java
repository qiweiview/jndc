package com.view.jndc.manage.enums.server;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public enum JNDCServerBindType {


    MOCK_SERVER("模拟服务", "mock_server"),


    ;


    public final String label;

    public final String value;


    JNDCServerBindType(String label, String value) {
        this.label = label;
        this.value = value;
    }

}
