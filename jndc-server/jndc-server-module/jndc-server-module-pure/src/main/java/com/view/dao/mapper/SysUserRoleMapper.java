package com.view.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.view.dao.entity.SysUserRole;
import org.springframework.stereotype.Repository;

/**
 * 用户和角色关联表(SysUserRole)表数据库访问层
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Repository
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

}

