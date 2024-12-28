package com.view.core.model.tcp_data;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum TCPResponse {
    SUCCESS("成功", "SUCCESS"),

    SERVICE_EXIST("服务存在", "SERVICE_EXIST"),

    SERVICE_NOT_EXIST("服务不存在", "SERVICE_NOT_EXIST"),

    REMOTE_CONNECTION_INTERRUPT("远程连接关闭", "REMOTE_CONNECTION_INTERRUPT"),

    ;

    public String label;

    public String value;

    TCPResponse(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
