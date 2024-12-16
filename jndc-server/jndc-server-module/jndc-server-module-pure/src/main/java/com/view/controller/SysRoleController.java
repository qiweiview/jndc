package com.view.controller;

import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.annotation.AdminPrefix;
import com.view.annotation.OperationLog;
import com.view.convert.role.RoleConvert;
import com.view.dao.entity.SysRole;
import com.view.dao.entity.SysUserRole;
import com.view.dto.role.RoleQueryDTO;
import com.view.dto.role.SysRoleCreateDTO;
import com.view.dto.role.SysRoleUpdateDTO;
import com.view.enums.OperBusinessType;
import com.view.enums.RoleEnum;
import com.view.enums.StatusCodeEnum;
import com.view.exception.ServiceException;
import com.view.model.vo.ResponseResult;
import com.view.service.SysRoleService;
import com.view.service.SysUserRoleService;
import com.view.utils.BeanCopyUtils;
import com.view.utils.StringUtils;
import com.view.vo.role.RoleSimpleVO;
import com.view.vo.role.SysRoleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

@AdminPrefix
@RequestMapping("/sysRole")
@RequiredArgsConstructor
public class SysRoleController {
    /**
     * 服务对象
     */

    private final SysRoleService sysRoleService;

    private final SysUserRoleService sysUserRoleService;

    /**
     * 查询角色列表
     *
     * @param queryDTO 查询实体
     * @return 所有数据
     */
    @GetMapping("/list")
    @SaCheckPermission("system:role:query")
    public ResponseResult<IPage<SysRoleVO>> selectPage(RoleQueryDTO queryDTO) {
        return ResponseResult.ok(this.sysRoleService.listRolePage(queryDTO));
    }

    /**
     * 角色详情
     *
     * @return 单条数据
     */
    @GetMapping("/get")
    public ResponseResult<SysRole> selectOne(@Valid @RequestBody SysRoleCreateDTO createDTO) {
        return ResponseResult.ok(this.sysRoleService.getById(createDTO.getId()));
    }

    /**
     * 新增角色
     *
     * @param createDTO 实体对象
     * @return 新增结果
     */
    @OperationLog(title = "角色管理",businessType = OperBusinessType.INSERT)
    @PostMapping("/create")
    @SaCheckPermission("system:role:create")
    public ResponseResult<Long> insert(@Valid @RequestBody SysRoleCreateDTO createDTO) {
        SysRole sysRole = BeanCopyUtils.copyBean(createDTO, SysRole.class);
        return ResponseResult.ok(this.sysRoleService.createRole(sysRole));
    }

    /**
     * 修改角色
     *
     * @param updateDTO 实体对象
     * @return 修改结果
     */
    @OperationLog(title = "角色管理",businessType = OperBusinessType.UPDATE)
    @PutMapping("/update")
    @SaCheckPermission("system:role:update")
    public ResponseResult<Long> update(@Valid @RequestBody SysRoleUpdateDTO updateDTO) {
        SysRole sysRole = BeanCopyUtils.copyBean(updateDTO, SysRole.class);
        return ResponseResult.ok(this.sysRoleService.updateRole(sysRole));
    }

    /**
     * 删除角色
     *
     * @return 删除结果
     */
    @OperationLog(title = "角色管理",businessType = OperBusinessType.DELETE)
    @DeleteMapping("/delete")
    @SaCheckPermission("system:role:delete")
    public ResponseResult<Boolean> delete(@RequestBody SysRoleCreateDTO createDTO) {
        Long id = createDTO.getId();
        if (Objects.nonNull(RoleEnum.fromValue(id))) {
            throw new ServiceException(StatusCodeEnum.FAIL.getCode(), "系统内置角色无法删除");
        }
        long count = sysUserRoleService.count(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id));
        if (count > 0) {
            throw new ServiceException(StatusCodeEnum.FAIL.getCode(), "该角色已绑定用户，无法删除");
        }
        return ResponseResult.ok(this.sysRoleService.removeById(id));
    }

    /**
     * 查询所有简单角色数据
     *
     * @param queryDTO 查询实体
     * @return 所有数据
     */
    @GetMapping("/listAll")
    @SaCheckPermission("system:role:listSimpleAll")
    public ResponseResult<List<RoleSimpleVO>> selectAll(RoleQueryDTO queryDTO) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<SysRole>().like(StringUtils.isNotEmpty(queryDTO.getRoleName()), SysRole::getRoleName, queryDTO.getRoleName());
        List<SysRole> list = sysRoleService.list(queryWrapper);
        return ResponseResult.ok(RoleConvert.INSTANCE.convertSimpleList(list));
    }
}

