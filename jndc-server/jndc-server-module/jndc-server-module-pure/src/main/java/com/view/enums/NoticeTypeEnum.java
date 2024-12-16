package com.view.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-15 14:55
 * @description: 通知公告类型
 */
@Getter
@AllArgsConstructor
public enum NoticeTypeEnum {
    /**
     * 通知类型
     */
    NOTIFICATION(1, "通知"),

    /**
     * 公告类型
     */
    PUBLIC(2, "公告");

    private final int type;

    private final String description;


    /**
     * 根据代码获取描述
     *
     * @param code 类型代码
     * @return 描述
     */
    public static String getDescriptionByCode(int code) {
        for (NoticeTypeEnum type : NoticeTypeEnum.values()) {
            if (type.getType() == code) {
                return type.getDescription();
            }
        }
        return null;
    }
}
