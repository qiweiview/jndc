package com.view.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.view.dao.entity.SysMenu;
import com.view.dao.entity.SysRole;
import com.view.dao.entity.SysRoleMenu;
import com.view.dao.entity.SysUserRole;
import com.view.dao.mapper.SysMenuMapper;
import com.view.dao.mapper.SysRoleMapper;
import com.view.dao.mapper.SysRoleMenuMapper;
import com.view.dao.mapper.SysUserRoleMapper;
import com.view.enums.BooleanEnum;
import com.view.enums.BusinessStatusEnum;
import com.view.enums.RoleEnum;
import com.view.exception.ServiceException;
import com.view.service.SysMenuService;
import com.view.vo.menu.AsyncRoutesMetaVO;
import com.view.vo.menu.AsyncRoutesVO;
import com.view.vo.menu.SimpleMenuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.view.enums.StatusCodeEnum.ROLE_FREEZE;

/**
 * 菜单权限表(SysMenu)表服务实现类
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Service
@RequiredArgsConstructor
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    private final SysMenuMapper menuMapper;

    private final SysRoleMenuMapper roleMenuMapper;

    private final SysUserRoleMapper userRoleMapper;

    private final SysRoleMapper roleMapper;




    @Override
    public List<AsyncRoutesVO> buildMenuTreeByRoles(Set<Long> roleIdList) {
        if (roleIdList.isEmpty()) {
            return Collections.emptyList();
        }
        List<SysMenu> sysMenuList = menuMapper.listRoleMenuByRoles(roleIdList, false);
        return buildMenuTree(sysMenuList);
    }

    @Override
    public List<SimpleMenuVO> listSimpleMenu() {
        return menuMapper.getSimpleMenuList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteById(Long menuId) {
        SysMenu sysMenu = menuMapper.selectById(menuId);
        if (Objects.isNull(sysMenu)) {
            throw new ServiceException("菜单不存在");
        }
        menuMapper.deleteById(sysMenu);
        List<Long> deleteMenuIdList = new ArrayList<>();
        deleteMenuIdList.add(sysMenu.getId());
        List<SysMenu> childList = menuMapper.selectList(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, sysMenu.getId()));
        if (!childList.isEmpty()){
            List<Long> childIdList = childList.stream().map(SysMenu::getId).collect(Collectors.toList());

            menuMapper.deleteBatchIds(childIdList);
            deleteMenuIdList.addAll(childIdList);
        }
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getMenuId,deleteMenuIdList));
        return true;
    }

    @Override
    public List<AsyncRoutesVO> getAsyncRoutes() {
        Long loginId = Long.parseLong(StpUtil.getLoginId().toString());


        // 查询用户角色
        List<SysUserRole> userRoleList = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, loginId));
        Set<Long> roleIdList = userRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());

        // 查看所属角色是否被禁用
        List<SysRole> sysRoles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIdList));

        ArrayList<String> roleCodeList = new ArrayList<>();
        boolean existDisable = false;
        boolean hasSuperAdmin = false;

        // 遍历角色
        for (SysRole role : sysRoles) {

            // 角色code
            roleCodeList.add(role.getRoleCode());

            // 判断是否有被禁用的角色
            if (role.getStatus() == BusinessStatusEnum.DISABLED.getValue()) {
                existDisable = true;
            }

            // 判断是否有超级管理员
            if (role.getId().equals(RoleEnum.SUPERADMIN.getId())) {
                hasSuperAdmin = true;
            }
        }

        // 如果有被禁用的角色，且没有超级管理员
        if (existDisable && !hasSuperAdmin) {
            throw new ServiceException(ROLE_FREEZE);
        }

        // 构建菜单树
        List<AsyncRoutesVO> asyncRoutesVOList = buildMenuTreeByRoles(roleIdList);

        return asyncRoutesVOList;
    }

    /**
     * 根据菜单列表构建菜单树
     *
     * @param menuList 菜单列表
     * @return 菜单树
     */
    private List<AsyncRoutesVO> buildMenuTree(List<SysMenu> menuList) {
        List<AsyncRoutesVO> rootNodes = new ArrayList<>();
        for (SysMenu menu : menuList) {
            if (menu.getParentId() == null || menu.getParentId() == 0) {
                rootNodes.add(buildMenuNode(menu, menuList));
            }
        }
        // 对根节点进行排序
        rootNodes.sort(Comparator.comparingInt(o -> o.getMeta().getSortOrder()));
        return rootNodes;
    }

    private AsyncRoutesVO buildMenuNode(SysMenu menu, List<SysMenu> menuList) {
        // 前端所需字段
        AsyncRoutesVO node = new AsyncRoutesVO();
        node.setPath(menu.getPath());
        node.setName(menu.getName());
        node.setComponent(menu.getComponent());
        node.setRedirect(menu.getRedirect());
        node.setType(menu.getType());
        // 设置路由元信息
        AsyncRoutesMetaVO meta = new AsyncRoutesMetaVO();
        meta.setTitle(menu.getTitle());
        meta.setIcon(menu.getIcon());
        meta.setSortOrder(menu.getSortOrder());
        meta.setKeepAlive(BooleanEnum.fromValue(menu.getCacheFlag()));
        meta.setFrameLoading(BooleanEnum.fromValue(menu.getFrameLoading()));
        meta.setAuths(Optional.ofNullable(menu.getPerms())
                .map(List::of)
                .orElse(Collections.emptyList()));
        meta.setFrameSrc(menu.getFrameSrc());
        node.setMeta(meta);
        // 递归构建子节点
        List<AsyncRoutesVO> children = new ArrayList<>();
        for (SysMenu childMenu : menuList) {
            if (childMenu.getParentId() != null && childMenu.getParentId().equals(menu.getId())) {
                children.add(buildMenuNode(childMenu, menuList));
            }
        }
        children.sort(Comparator.comparingInt(o -> o.getMeta().getSortOrder()));
        node.setChildren(children);
        return node;
    }
}

