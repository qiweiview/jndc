package com.view.service;

import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Set;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-01 20:40
 * @description: 权限
 */
public interface PermissionService {

    /**
     * 为角色分配菜单
     */
    void assignMenu(Long roleId, Set<Long> menuIds);

    /**
     * 获取角色所分配菜单ID
     */
    List<Long> getMenuIdListByRole(@PathVariable Long roleId);

    /**
     * 获取用户所分配角色
     * @param userId
     * @return
     */
    List<Long> getRoleIdsByUser(Long userId);

    /**
     * 分配角色
     * @param userId
     * @param roleIds
     */
    void assignRole(Long userId, Set<Long> roleIds);

    /**
     * 根据角色列表获取按钮菜单权限
     * @param roleIds 角色Ids
     * @return perms
     */
    Set<String> getMenuPermissionByRoles(Set<Long> roleIds);
}
