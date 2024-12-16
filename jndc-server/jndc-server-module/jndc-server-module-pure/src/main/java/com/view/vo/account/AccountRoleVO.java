package com.view.vo.account;

import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-03 15:56
 * @description: 用户所拥有角色
 */
@Data
public class AccountRoleVO {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;
}
