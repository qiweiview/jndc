package com.view.core.enum_value;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum TCPDataTyps {
    DATA("数据", "DATA"),
    ACTIVE("激活", "ACTIVE"),
    INACTIVE("失效", "INACTIVE");

    public String label;

    public String value;

    TCPDataTyps(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
