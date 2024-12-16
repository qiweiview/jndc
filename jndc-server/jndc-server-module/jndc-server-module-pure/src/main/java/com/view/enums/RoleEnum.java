package com.view.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-09 16:11
 * @description: 初始角色枚举
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

    /**
     * 超级管理员
     */
    SUPERADMIN(1L, "超级管理员", "super-admin"),
    /**
     * 管理员
     */
    ADMIN(2L, "管理员", "admin"),
    /**
     * 普通用户
     */
    USER(3L, "普通用户", "user");

    /**
     * 角色id
     */
    private final Long id;

    /**
     * 角色名称
     */
    private final String roleName;

    /**
     * 角色编码
     */
    private final String roleCode;

    /**
     * 根据角色ID获取角色枚举。
     *
     * @param id 角色ID
     * @return 对应的角色枚举，如果没有匹配则返回null
     */
    public static RoleEnum fromValue(Long id) {
        if (id == null) {
            return null;
        }
        for (RoleEnum role : RoleEnum.values()) {
            if (role.getId().equals(id)) {
                return role;
            }
        }
        return null;
    }

    /**
     * 根据角色编码获取角色枚举。
     *
     * @param code 角色编码
     * @return 对应的角色枚举，如果没有匹配则返回null
     */
    public static RoleEnum fromCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        for (RoleEnum role : RoleEnum.values()) {
            if (role.getRoleCode().equals(code)) {
                return role;
            }
        }
        return null;
    }
}
