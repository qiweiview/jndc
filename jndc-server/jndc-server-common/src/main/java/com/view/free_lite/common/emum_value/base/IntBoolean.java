package com.view.free_lite.common.emum_value.base;

public enum IntBoolean {

    TRUE(1),

    FALSE(0),

    ;

    public int code;


    private IntBoolean(int code) {
        this.code = code;
    }

}
