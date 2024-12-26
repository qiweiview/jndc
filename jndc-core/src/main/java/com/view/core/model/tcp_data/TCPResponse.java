package com.view.core.model.tcp_data;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum TCPResponse {
    SUCCESS("成功", "SUCCESS"),

    SERVICE_EXIST("服务存在", "SERVICE_EXIST"),

    SERVICE_NOT_EXIST("服务不存在", "SERVICE_NOT_EXIST"),


    ;

    public String label;

    public String value;

    TCPResponse(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
