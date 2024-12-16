package com.view.free_lite.common.emum_value.base;

public enum DataState {

    LOGICAL_DELETED(-1),

    ENABLE(1),

    ;

    public int code;


    private DataState(int code) {
        this.code = code;
    }

}
