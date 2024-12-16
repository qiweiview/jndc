package com.view.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.view.dao.entity.SysRoleMenu;
import com.view.dao.mapper.SysRoleMenuMapper;
import com.view.service.SysRoleMenuService;
import org.springframework.stereotype.Service;

/**
 * 角色和菜单关联表(SysRoleMenu)表服务实现类
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Service
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements SysRoleMenuService {

}

