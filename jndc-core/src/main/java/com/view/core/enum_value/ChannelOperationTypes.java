package com.view.core.enum_value;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum ChannelOperationTypes {
    INACTIVE("部署", "INACTIVE"),


    ;

    public String label;

    public String value;

    ChannelOperationTypes(String label, String value) {
        this.label = label;
        this.value = value;
    }
}
