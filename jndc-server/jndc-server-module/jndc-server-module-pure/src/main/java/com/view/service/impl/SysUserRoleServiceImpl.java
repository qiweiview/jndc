package com.view.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.view.dao.entity.SysUserRole;
import com.view.dao.mapper.SysUserRoleMapper;
import com.view.service.SysUserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户和角色关联表(SysUserRole)表服务实现类
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {

}

