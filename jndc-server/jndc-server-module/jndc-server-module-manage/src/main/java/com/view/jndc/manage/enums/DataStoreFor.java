package com.view.jndc.manage.enums;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public enum DataStoreFor {


    SERVER("服务端", "server"),

    CLIENT("客户端", "client"),


    ;


    public final String label;

    public final String value;


    DataStoreFor(String label, String value) {
        this.label = label;
        this.value = value;
    }

}
