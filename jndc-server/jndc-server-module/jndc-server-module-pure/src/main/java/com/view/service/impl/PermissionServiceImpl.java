package com.view.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.view.dao.entity.*;
import com.view.dao.mapper.*;
import com.view.enums.ReadStatusEnum;
import com.view.enums.StatusCodeEnum;
import com.view.exception.ServiceException;
import com.view.service.*;
import com.view.utils.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.view.utils.CollectionUtils.convertSet;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-01 20:40
 * @description: 权限相关
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SysRoleMenuService roleMenuService;

    private final SysMenuMapper menuMapper;

    private final SysRoleMenuMapper sysRoleMenuMapper;

    private final SysRoleMapper sysRoleMapper;

    private final SysUserRoleMapper userRoleMapper;

    private final SysUserRoleService userRoleService;

    private final SysNoticeRoleMapper noticeRoleMapper;

    private final SysNoticeUserReadMapper noticeUserReadMapper;

    private final SysNoticeRoleService noticeRoleService;

    private final SysNoticeUserReadService noticeUserReadService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenu(Long roleId, Set<Long> menuIds) {
        SysRole sysRole = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getId, roleId));
        if (Objects.isNull(sysRole)) {
            throw new ServiceException(StatusCodeEnum.FAIL.getCode(), "该角色不存在");
        }
        // 为空删除所有
        if (CollUtil.isEmpty(menuIds)) {
            sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
            return;
        }
        // 获得角色拥有菜单编号
        Set<Long> dbMenuIds = convertSet(sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>().
                eq(SysRoleMenu::getRoleId, roleId)), SysRoleMenu::getMenuId);
        // 计算新增和删除的菜单编号
        Set<Long> menuIdList = CollUtil.emptyIfNull(menuIds);
        Collection<Long> createMenuIds = CollUtil.subtract(menuIdList, dbMenuIds);
        Collection<Long> deleteMenuIds = CollUtil.subtract(dbMenuIds, menuIdList);
        // 执行新增和删除。对于已经授权的菜单，不用做任何处理
        if (CollUtil.isNotEmpty(createMenuIds)) {
            roleMenuService.saveBatch(CollectionUtils.convertList(createMenuIds, menuId -> {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenu.setMenuId(menuId);
                return sysRoleMenu;
            }));
        }
        if (CollUtil.isNotEmpty(deleteMenuIds)) {
            sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().
                    eq(SysRoleMenu::getRoleId, roleId).in(SysRoleMenu::getMenuId, deleteMenuIds));
        }

    }

    @Override
    public List<Long> getMenuIdListByRole(Long roleId) {
        List<SysRoleMenu> list = roleMenuService.list(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        return list.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getRoleIdsByUser(Long userId) {
        List<SysUserRole> sysUserRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userId));
        return sysUserRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRole(Long userId, Set<Long> roleIds) {
        // 获得角色拥有角色编号
        Set<Long> dbRoleIds = convertSet(userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)),
                SysUserRole::getRoleId);
        // 计算新增和删除的角色编号
        Set<Long> roleIdList = CollUtil.emptyIfNull(roleIds);
        Collection<Long> createRoleIds = CollUtil.subtract(roleIdList, dbRoleIds);
        Collection<Long> deleteRoleIds = CollUtil.subtract(dbRoleIds, roleIdList);
        // 执行新增和删除。对于已经授权的角色，不用做任何处理
        if (!CollectionUtil.isEmpty(createRoleIds)) {
            userRoleService.saveBatch(CollectionUtils.convertList(createRoleIds, roleId -> {
                SysUserRole entity = new SysUserRole();
                entity.setUserId(userId);
                entity.setRoleId(roleId);
                return entity;
            }));
            dbRoleIds.addAll(createRoleIds);
        }
        if (!CollectionUtil.isEmpty(deleteRoleIds)) {
            userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().
                    eq(SysUserRole::getUserId, userId).
                    in(SysUserRole::getRoleId, deleteRoleIds));
            dbRoleIds.removeAll(deleteRoleIds);
        }
        updateUserNotificationRead(userId, createRoleIds, deleteRoleIds);
    }

    /**
     * 获取按钮权限菜单
     * @param roleIds 角色Ids
     * @return
     */
    @Override
    public Set<String> getMenuPermissionByRoles(Set<Long> roleIds) {
        if (CollectionUtil.isEmpty(roleIds)) {
            return CollectionUtil.newHashSet();
        }
        List<SysMenu> sysMenus = menuMapper.listRoleMenuByRoles(roleIds, true);
        return sysMenus.stream().map(SysMenu::getPerms).collect(Collectors.toSet());
    }

    /**
     * 重新设置阅读表
     */
    private void updateUserNotificationRead(Long userId, Collection<Long> createRoleIds, Collection<Long> deleteRoleIds) {
        // 处理新增角色通知
        if (!createRoleIds.isEmpty()) {
            // 获得所有需要添加的通知ID
            List<Long> newNoticeIds = getNoticesByRoleIds(createRoleIds);
            if (!newNoticeIds.isEmpty()) {
                // 批量插入新通知记录
                List<SysNoticeUserRead> newRecords = newNoticeIds.stream()
                        .map(noticeId -> SysNoticeUserRead.builder().noticeId(noticeId).userId(userId).status(ReadStatusEnum.UNREAD.getCode()).build())
                        .collect(Collectors.toList());
                noticeUserReadService.saveBatch(newRecords);
            }
        }
        // 处理删除角色通知
        if (!deleteRoleIds.isEmpty()) {
            // 获得所有需要删除的通知ID
            List<Long> oldNoticeIds = getNoticesByRoleIds(deleteRoleIds);
            if (!oldNoticeIds.isEmpty()) {
                // 批量删除旧通知记录
                noticeUserReadMapper.delete(new LambdaQueryWrapper<SysNoticeUserRead>()
                        .eq(SysNoticeUserRead::getUserId, userId)
                        .in(SysNoticeUserRead::getNoticeId, oldNoticeIds));
            }
        }
    }
    private List<Long> getNoticesByRoleIds(Collection<Long> roleIds) {
        // 查询所有角色的通知ID
        return noticeRoleMapper.selectList(new LambdaQueryWrapper<SysNoticeRole>()
                        .in(SysNoticeRole::getRoleId, roleIds))
                .stream()
                .map(SysNoticeRole::getNoticeId)
                .distinct()
                .collect(Collectors.toList());
    }
}
