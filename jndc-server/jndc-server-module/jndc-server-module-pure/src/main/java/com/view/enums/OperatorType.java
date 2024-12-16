package com.view.enums;

import lombok.Getter;

/**
 * 操作人类别
 *
 * @author ruoyi
 */
@Getter
public enum OperatorType {

    /**
     * 其它
     */
    OTHER(0),

    /**
     * 后台用户
     */
    MANAGE(1);


    private final int value;

    OperatorType(int value) {
        this.value = value;
    }

}
