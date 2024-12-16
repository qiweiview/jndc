package com.view.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.view.dao.entity.SysMenu;
import com.view.vo.menu.SimpleMenuVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;


/**
 * 菜单权限表(SysMenu)表数据库访问层
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Repository
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据角色ID获取菜单（路由菜单或权限菜单）
     * @param roleIdList 用户所属角色ID数组
     * @param isPerms 是否是权限菜单
     * @return 菜单列表
     */
    List<SysMenu> listRoleMenuByRoles(@Param("roleIdList") Set<Long> roleIdList, @Param("isPerms") Boolean isPerms);

    /**
     * 简单菜单
     * @return 列表
     */
    List<SimpleMenuVO> getSimpleMenuList();
}

