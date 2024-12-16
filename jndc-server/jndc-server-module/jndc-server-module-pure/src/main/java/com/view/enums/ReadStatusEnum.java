package com.view.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-16 14:44
 * @description: 公告通知已读未读状态
 */
@Getter
@AllArgsConstructor
public enum ReadStatusEnum {

    /**
     * 未读
     */
    UNREAD(0, "未读"),

    /**
     * 已读
     */
    READ(1, "已读");

    /**
     * 状态
     */
    private final int code;

    /**
     * 描述
     */
    private final String description;

    public static ReadStatusEnum fromCode(int code) {
        for (ReadStatusEnum status : ReadStatusEnum.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知状态: " + code);
    }

    @Override
    public String toString() {
        return this.description;
    }
}
