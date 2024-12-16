package com.view.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-09 16:11
 * @description: 0对应false否，1对应true是
 */
@AllArgsConstructor
public enum BooleanEnum {
    /**
     * 代表false值。
     */
    FALSE(0),

    /**
     * 代表true值。
     */
    TRUE(1);

    @Getter
    private final int value;

    /**
     * 将给定对象转换为布尔值。
     * 接受的类型有Integer和String。
     * 对于Integer，0对应false，1对应true。
     * 对于String，"0"对应false，"1"对应true。
     *
     * @param value 要转换的对象
     * @return 对应输入的布尔值
     * @throws IllegalArgumentException 如果输入不是有效的Integer或String
     */
    public static boolean fromValue(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof Integer intValue) {
            return intValue == TRUE.getValue();
        }
        if (value instanceof String strValue) {
            return String.valueOf(TRUE.getValue()).equals(strValue);
        }
        throw new IllegalArgumentException("无效值: " + value);
    }
}
