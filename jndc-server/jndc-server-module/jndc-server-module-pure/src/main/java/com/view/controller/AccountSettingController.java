package com.view.controller;

import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.view.annotation.AdminPrefix;
import com.view.convert.role.RoleConvert;
import com.view.dao.entity.SysRole;
import com.view.dao.entity.SysUser;
import com.view.dto.account.AccountUpdateDTO;
import com.view.dto.account.EmailUpdateDTO;
import com.view.dto.account.PasswordUpdateDTO;
import com.view.exception.ServiceException;
import com.view.model.vo.ResponseResult;
import com.view.service.AccountSettingService;
import com.view.service.SysRoleService;
import com.view.service.SysUserService;
import com.view.utils.BeanCopyUtils;
import com.view.utils.StringUtils;
import com.view.vo.account.AccountRoleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-03 15:46
 * @description: 用户账号设置
 */
@AdminPrefix
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/account")
public class AccountSettingController {

    private final SysRoleService roleService;

    private final SysUserService userService;

    private final AccountSettingService accountSettingService;

    /**
     * 获取登录用户所分配角色
     *
     * @return 所有数据
     */
    @GetMapping("/getRole")
    public ResponseResult<List<AccountRoleVO>> getRoleByLoginUser() {
        List<String> roleCodeList = StpUtil.getRoleList();
        List<SysRole> roleList = roleService.list(new LambdaQueryWrapper<SysRole>().in(SysRole::getRoleCode, roleCodeList));
        return ResponseResult.ok(RoleConvert.INSTANCE.convertAccountRoleList(roleList) );
    }

    /**
     * 修改个人信息
     */
    @PutMapping("/updateInfo")
    public ResponseResult<Boolean> update(@Valid @RequestBody AccountUpdateDTO updateDTO) {
        SysUser sysUser = BeanCopyUtils.copyBean(updateDTO, SysUser.class);
        Long loginId = Long.valueOf(StpUtil.getLoginId().toString());
        sysUser.setId(loginId);
        sysUser.setUpdateBy(loginId);
        return ResponseResult.ok(userService.updateById(sysUser));
    }

    /**
     * 发送更改密码验证码
     */
    @GetMapping("/sendPwdCode")
    public ResponseResult<Void> sendPwdCode() {
        accountSettingService.sendCode(null);
        return ResponseResult.ok();
    }

    /**
     * 发送更改邮箱验证码
     */
    @PostMapping("/sendEmailCode")
    public ResponseResult<Void> sendEmailCode(@RequestBody AccountUpdateDTO updateDTO) {
        String email=updateDTO.getEmail();
        if (StringUtils.isNotEmpty(email)&&!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")){
            throw new ServiceException("邮箱格式不正确");
        }
        accountSettingService.sendCode(email);
        return ResponseResult.ok();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public ResponseResult<Void> updatePassword(@Valid @RequestBody PasswordUpdateDTO updateDTO) {
        accountSettingService.updatePassword(updateDTO);
        return ResponseResult.ok();
    }

    /**
     * 修改密码
     */
    @PutMapping("/updateEmail")
    public ResponseResult<Void> updateEmail(@Valid @RequestBody EmailUpdateDTO updateDTO) {
        accountSettingService.updateEmail(updateDTO);
        return ResponseResult.ok();
    }
}
