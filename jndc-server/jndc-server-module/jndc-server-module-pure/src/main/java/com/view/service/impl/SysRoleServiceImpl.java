package com.view.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.view.dao.entity.SysRole;
import com.view.dao.mapper.SysRoleMapper;
import com.view.dto.role.RoleQueryDTO;
import com.view.enums.BusinessStatusEnum;
import com.view.enums.RoleEnum;
import com.view.enums.StatusCodeEnum;
import com.view.exception.ServiceException;
import com.view.service.SysRoleService;
import com.view.vo.role.SysRoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 角色信息表(SysRole)表服务实现类
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMapper sysRoleMapper;

    @Override
    public IPage<SysRoleVO> listRolePage(RoleQueryDTO queryDTO) {
        Page<SysRoleVO> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        return sysRoleMapper.selectRolePage(page, queryDTO);
    }

    @Override
    public Long createRole(SysRole sysRole) {
        validateRoleUniqueness(null, sysRole.getRoleName(), sysRole.getRoleCode());
        sysRole.setCreateBy(Long.valueOf(StpUtil.getLoginId().toString()));
        sysRoleMapper.insert(sysRole);
        return sysRole.getId();
    }

    @Override
    public Long updateRole(SysRole sysRole) {
        if (Objects.nonNull(RoleEnum.fromValue(sysRole.getId())) && sysRole.getStatus().equals(BusinessStatusEnum.DISABLED.getValue())) {
            throw new ServiceException(StatusCodeEnum.FAIL.getCode(), "系统内置角色无法停用");
        }
        SysRole exists = sysRoleMapper.selectById(sysRole.getId());
        if (exists == null) {
            throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "修改的角色不存在");
        }

        validateRoleUniqueness(sysRole.getId(), sysRole.getRoleName(), sysRole.getRoleCode());
        sysRole.setUpdateBy(Long.valueOf(StpUtil.getLoginId().toString()));
        sysRoleMapper.updateById(sysRole);
        return sysRole.getId();
    }

    @Override
    public boolean hasSuperAdmin(List<Long> ids) {
        for (Long id : ids) {
            if (id.equals(RoleEnum.SUPERADMIN.getId())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 校验角色名称和编码的唯一性
     *
     * @param id   角色ID，用于排除自身
     * @param name 角色名称
     * @param code 角色编码
     */
    private void validateRoleUniqueness(Long id, String name, String code) {
        SysRole sysRoleName = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleName, name));
        if (sysRoleName != null && !sysRoleName.getId().equals(id)) {
            throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "角色名称不能重复");
        }
        SysRole sysRoleCode = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleCode, code));
        if (sysRoleCode != null && !sysRoleCode.getId().equals(id)) {
            throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "角色编码不能重复");
        }
    }

}

