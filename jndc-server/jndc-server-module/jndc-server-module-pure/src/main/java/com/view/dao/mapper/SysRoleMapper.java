package com.view.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.dao.entity.SysRole;
import com.view.dto.role.RoleQueryDTO;
import com.view.vo.role.SysRoleVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色信息表(SysRole)表数据库访问层
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Repository
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 自定义分页查询
     * @param page 分页对象
     * @param query 查询对象
     * @return 查询结果
     */
    IPage<SysRoleVO> selectRolePage(IPage<SysRoleVO> page,@Param("query")RoleQueryDTO query);

    /**
     * 根据用户Id获取角色集合
     */
    public List<String> listRolesByUserId(@Param("userId") Long userId);
}

