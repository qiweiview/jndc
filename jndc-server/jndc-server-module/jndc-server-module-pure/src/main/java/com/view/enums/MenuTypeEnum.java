package com.view.enums;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-02 21:13
 * @description: 菜单类型
 */
public enum MenuTypeEnum {
    /**
     * 目录
     */
    DIRECTORY(0, "目录"),

    /**
     * 菜单
     */
    MENU(1, "菜单"),

    /**
     * Iframe
     */
    IFRAME(2, "Iframe"),

    /**
     * 外链
     */
    EXTERNAL_LINK(3, "外链"),

    /**
     * 按钮
     */
    BUTTON(4, "按钮");

    private final int type;
    private final String typeStr;

    MenuTypeEnum(int type, String typeStr) {
        this.type = type;
        this.typeStr = typeStr;
    }

    public int getType() {
        return type;
    }

    public String getTypeStr() {
        return typeStr;
    }

    /**
     * 根据type获取对应的typeStr，如果找不到返回空字符串
     * @param type 类型
     * @return 类型字符串
     */
    public static String getTypeStrByType(int type) {
        for (MenuTypeEnum menuType : MenuTypeEnum.values()) {
            if (menuType.getType() == type) {
                return menuType.getTypeStr();
            }
        }
        // 返回默认的类型字符串
        return "";
    }
}
