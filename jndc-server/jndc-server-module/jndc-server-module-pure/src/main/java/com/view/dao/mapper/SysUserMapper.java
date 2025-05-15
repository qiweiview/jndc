package com.view.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.dao.entity.SysUser;
import com.view.dto.user.UserQueryDTO;
import com.view.vo.user.SysUserVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户信息表(SysUser)表数据库访问层
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Repository
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 自定义分页查询
     * @param page 分页对象
     * @param query 查询对象
     * @return 查询结果
     */
    IPage<SysUserVO> selectUserVOPage(IPage<SysUserVO> page, @Param("query") UserQueryDTO query);

    List<SysUser> selectByPhone(String userPhoneNumber);

    SysUser selectByUserName(@Param("username")String username);

}

