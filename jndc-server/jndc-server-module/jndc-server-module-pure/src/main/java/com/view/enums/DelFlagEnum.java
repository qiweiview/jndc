package com.view.enums;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-06-26 21:20
 * @description: 逻辑删除状态
 */
public enum DelFlagEnum {

    /**
     * 未删除
     */
    NOT_DELETED(0),

    /**
     * 已删除
     */
    DELETED(1);

    private final int value;

    DelFlagEnum(int value) {
        this.value = value;
    }
}
