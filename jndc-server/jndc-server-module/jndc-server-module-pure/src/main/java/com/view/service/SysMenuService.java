package com.view.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.view.dao.entity.SysMenu;
import com.view.vo.menu.AsyncRoutesVO;
import com.view.vo.menu.SimpleMenuVO;

import java.util.List;
import java.util.Set;

/**
 * 菜单权限表(SysMenu)表服务接口
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
public interface SysMenuService extends IService<SysMenu> {


    /**
     * 根据角色ID列表获取菜单
     * @param roleIdList 角色ID
     * @return AsyncRoutesVO
     */
    List<AsyncRoutesVO> buildMenuTreeByRoles(Set<Long> roleIdList);

    /**
     * 简单菜单，授予角色菜单用
     * @return SimpleMenuVO
     */
    List<SimpleMenuVO> listSimpleMenu();

    /**
     * 删除菜单，连同子菜单
     */
    Boolean deleteById(Long menuId);
}

