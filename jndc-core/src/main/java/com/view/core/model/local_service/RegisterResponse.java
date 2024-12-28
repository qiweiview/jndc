package com.view.core.model.local_service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum RegisterResponse {
    SUCCESS("成功", "SUCCESS"),

    CLIENT_NOT_EXIST("客户端不存在", "SERVICE_NOT_EXIST"),

    SERVICE_EXIST("服务存在", "SERVICE_EXIST"),

    SERVICE_NOT_EXIST("服务不存在", "SERVICE_NOT_EXIST"),

    PORT_HAS_BOUND("端口已经绑定", "PORT_HAS_BOUND"),

    TCP_SERVER_START_FAIL("服务启动异常", "TCP_SERVER_START_FAIL"),



    ;

    public String label;

    public String value;

    RegisterResponse(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
