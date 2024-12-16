package com.view.vo.role;

import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-01 21:57
 * @description: 角色简单对象
 */
@Data
public class RoleSimpleVO {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;
}
