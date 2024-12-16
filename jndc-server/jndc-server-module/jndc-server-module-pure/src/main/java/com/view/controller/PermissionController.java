package com.view.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.view.annotation.AdminPrefix;
import com.view.annotation.OperationLog;
import com.view.dto.roleMenu.RoleMenuAssignDTO;
import com.view.dto.userRole.UserRoleAssignDTO;
import com.view.enums.OperBusinessType;
import com.view.model.vo.ResponseResult;
import com.view.service.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;


@AdminPrefix
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/permission")
public class PermissionController {

    private final PermissionService permissionService;
    /**
     * 获取角色已分配菜单
     *
     * @return 所有数据
     */

    @PostMapping("/getMenuIdList")
    @SaCheckPermission("system:permission:getMenus")
    public ResponseResult<List<String>> getMenuIdList( @RequestBody RoleMenuAssignDTO assignDTO){
        List<Long> menuIdListByRole = permissionService.getMenuIdListByRole(assignDTO.getRoleId());
        List<String> collect = menuIdListByRole.stream().map(x -> x.toString()).collect(Collectors.toList());
        return ResponseResult.ok(collect);
    }

    /**
     * 为角色分配菜单
     */
    @OperationLog(title = "权限分配",businessType = OperBusinessType.GRANT)
    @PostMapping("/assignForRole")
    @SaCheckPermission("system:permission:assignMenu")
    public ResponseResult<Void> assignForRole(@Valid @RequestBody RoleMenuAssignDTO assignDTO){
        permissionService.assignMenu(assignDTO.getRoleId(),assignDTO.getMenuIds());
        return ResponseResult.ok();
    }

    /**
     * 获取用户所分配角色
     *
     * @return 所有数据
     */
    @PostMapping("/getRoleIds")
    @SaCheckPermission("system:permission:getRoles")
    public ResponseResult<List<Long>> getRoleIds( @RequestBody RoleMenuAssignDTO assignDTO){
        return ResponseResult.ok( permissionService.getRoleIdsByUser(assignDTO.getUserId()));
    }

    /**
     * 为用户分配角色
     */
    @OperationLog(title = "权限分配",businessType = OperBusinessType.GRANT)
    @PostMapping("/assignRoleForUser")
    @SaCheckPermission("system:permission:assignRole")
    public ResponseResult<Void> assignRoleForUser(@Valid @RequestBody UserRoleAssignDTO assignDTO){
        permissionService.assignRole(assignDTO.getUserId(),assignDTO.getRoleIds());
        return ResponseResult.ok();
    }
}
