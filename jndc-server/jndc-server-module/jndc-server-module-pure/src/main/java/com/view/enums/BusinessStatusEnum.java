package com.view.enums;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-02 15:29
 * @description: 业务状态
 */
public enum BusinessStatusEnum {

    /**
     * 正常
     */
    ACTIVE(0),

    /**
     * 禁用
     */
    DISABLED(1);

    private final int value;

    BusinessStatusEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
