package com.view.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.view.dao.entity.SysRole;
import com.view.dto.role.RoleQueryDTO;
import com.view.vo.role.SysRoleVO;

import java.util.List;

/**
 * 角色信息表(SysRole)表服务接口
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
public interface SysRoleService extends IService<SysRole> {


    /**
     * 分页查询
     * @param queryDTO 查询对象
     * @return 返回值
     */
    IPage<SysRoleVO> listRolePage(RoleQueryDTO queryDTO);

    /**
     * 角色创建
     * @param sysRole 实体对象
     * @return ID
     */
    Long createRole(SysRole sysRole);

    /**
     * 角色修改
     * @param sysRole 实体对象
     * @return ID
     */
    Long updateRole(SysRole sysRole);

    /**
     * 角色是否有超级管理员
     * @param ids 角色ID
     * @return
     */
    boolean hasSuperAdmin(List<Long> ids);

}

