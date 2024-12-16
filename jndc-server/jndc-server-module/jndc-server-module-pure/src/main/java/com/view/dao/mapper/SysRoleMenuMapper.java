package com.view.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.view.dao.entity.SysRoleMenu;
import com.view.vo.roleMenu.RoleMenuGrantVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色和菜单关联表(SysRoleMenu)表数据库访问层
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Repository
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 获取全部菜单，未分配菜单的角色返回的roleMenuId为空
     * @param roleId 角色ID
     * @return
     */
    List<RoleMenuGrantVO> listRoleMenuByRoleId(Long roleId);

}

