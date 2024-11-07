package com.view.core.enum_value;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ServiceOperationTypes {
    DATA("数据", "DATA"),
    DEPLOY("部署", "DEPLOY"),
    WITHDRAW("撤销", "WITHDRAW");

    public String label;

    public String value;

    ServiceOperationTypes(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
