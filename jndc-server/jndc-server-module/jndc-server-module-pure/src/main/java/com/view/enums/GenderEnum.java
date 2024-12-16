package com.view.enums;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-02 15:24
 * @description: 性别枚举
 */
public enum GenderEnum {

    /**
     * 未知
     */
    UNKNOWN(0),

    /**
     * 男
     */
    MALE(1),

    /**
     * 女
     */
    FEMALE(2);

    private final int value;

    GenderEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
