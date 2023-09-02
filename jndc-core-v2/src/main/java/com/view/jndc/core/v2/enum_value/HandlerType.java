package com.view.jndc.core.v2.enum_value;

public enum HandlerType {
    SERVER_HANDLER("服务端类型", "SERVER_HANDLER"),
    CLIENT_HANDLER("客户端类型", "CLIENT_HANDLER"),
    ;

    public String name;
    public String value;


    HandlerType(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
