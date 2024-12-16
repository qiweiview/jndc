package com.view.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.view.dao.entity.SysUser;
import com.view.dao.entity.SysUserRole;
import com.view.dao.mapper.SysUserMapper;
import com.view.dao.mapper.SysUserRoleMapper;
import com.view.dto.user.UserQueryDTO;
import com.view.enums.BusinessStatusEnum;
import com.view.enums.RoleEnum;
import com.view.enums.StatusCodeEnum;
import com.view.exception.ServiceException;
import com.view.service.SysConfigService;
import com.view.service.SysRoleService;
import com.view.service.SysUserService;
import com.view.utils.StringUtils;
import com.view.vo.user.SysUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.view.constant.ConfigCacheKeyConstants.USER_DEFAULT_AVATAR;

/**
 * 用户信息表(SysUser)表服务实现类
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserMapper sysUserMapper;

    private final SysUserRoleMapper userRoleMapper;

    private final SysRoleService roleService;

    private final SysConfigService configService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(SysUser sysUser) {
        validateUserUniqueness(null, sysUser.getUsername(), sysUser.getEmail(), sysUser.getPhone());
        sysUser.setAvatar(configService.getConfigValueByKey(USER_DEFAULT_AVATAR));
        // 密码加密
        sysUser.setPassword(SaSecureUtil.md5(sysUser.getPassword()));
        sysUserMapper.insert(sysUser);
        userRoleMapper.insert(SysUserRole.builder().userId(sysUser.getId()).roleId(RoleEnum.USER.getId()).build());
        return sysUser.getId();
    }

    @Override
    public Long updateUser(SysUser sysUser) {

        // 是否存在
        SysUser exists = validateUserExists(sysUser.getId());
        // 有冻结的行为
        if (sysUser.getStatus().equals(BusinessStatusEnum.DISABLED.getValue())) {
            if (Long.valueOf(StpUtil.getLoginId().toString()).equals(sysUser.getId())){
                throw new ServiceException("无法冻结自身");
            }
            List<Long> roleIdList = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, sysUser.getId())).stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            if (roleService.hasSuperAdmin(roleIdList)){
                throw new ServiceException("无法冻结超级管理员");
            }
        }
        // 邮箱这些是否有重复
        validateUserUniqueness(sysUser.getId(), sysUser.getUsername(), sysUser.getEmail(), sysUser.getPhone());

        sysUser.setUpdateBy(Long.valueOf(StpUtil.getLoginId().toString()));
        sysUserMapper.updateById(sysUser);
        return sysUser.getId();
    }

    @Override
    public IPage<SysUserVO> pageUserVO(UserQueryDTO query) {
        Page<SysUserVO> page = new Page<>(query.getCurrent(), query.getSize());
        return sysUserMapper.selectUserVOPage(page, query);
    }

    @Override
    public Integer deleteByIds(List<Long> idList) {
        Long loginId = Long.valueOf(StpUtil.getLoginId().toString());
        for (Long id : idList) {
            if (id.equals(loginId)) {
                throw new ServiceException("无法删除自身");
            }
        }
        this.removeByIds(idList);
        return idList.size();
    }

    @Override
    public void resetPassword(SysUser sysUser) {
        SysUser user = validateUserExists(sysUser.getId());
        user.setPassword(SaSecureUtil.md5(sysUser.getPassword()));
        sysUserMapper.updateById(user);
    }

    private SysUser validateUserExists(Long id) {
        if (id == null) {
            return null;
        }
        SysUser user = sysUserMapper.selectById(id);
        if (Objects.isNull(user)) {
            throw new ServiceException("用户不存在");
        }
        return user;
    }

    /**
     * 校验用户名的唯一性
     *
     * @param id   用户ID，用于排除自身
     * @param name 用户名
     */
    private void validateUserUniqueness(Long id, String name, String email, String phone) {
        SysUser sysUserName = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, name));
        if (sysUserName != null && !sysUserName.getId().equals(id)) {
            throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "该用户名已存在");
        }
        if (StringUtils.isNotEmpty(email)) {
            SysUser sysUserEmail = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, email));
            if (sysUserEmail != null && !sysUserEmail.getId().equals(id)) {
                throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "该邮箱已被用户：" + sysUserEmail.getUsername() + "绑定");
            }
        }
        if (StringUtils.isNotEmpty(phone)) {
            SysUser sysUserPhone = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getPhone, phone));
            if (sysUserPhone != null && !sysUserPhone.getId().equals(id)) {
                throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "该手机号已被用户：" + sysUserPhone.getUsername() + "绑定");
            }
        }

    }
}

