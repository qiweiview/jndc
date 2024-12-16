package com.view.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.view.dao.entity.SysUser;
import com.view.dto.user.UserQueryDTO;
import com.view.vo.user.SysUserVO;

import java.util.List;

/**
 * 用户信息表(SysUser)表服务接口
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 创建用户
     * @param sysUser 创建对象
     * @return ID
     */
    Long createUser(SysUser sysUser);

    /**
     * 修改用户
     * @param sysUser 修改对象
     * @return ID
     */
    Long updateUser(SysUser sysUser);

    /**
     * 用户查询分页
     * @param queryDTO 查询对象
     * @return SysUserVO
     */
    IPage<SysUserVO> pageUserVO(UserQueryDTO queryDTO);

    /**
     * 逻辑删除用户
     * @param idList 用户ID
     * @return 删除数量
     */
    Integer deleteByIds(List<Long> idList);

    /**
     * 重置密码
     */
    void resetPassword(SysUser sysUser);
}

