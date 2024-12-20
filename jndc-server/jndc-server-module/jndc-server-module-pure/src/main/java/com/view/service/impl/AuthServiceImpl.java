package com.view.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.view.dao.entity.SysRole;
import com.view.dao.entity.SysUser;
import com.view.dao.entity.SysUserRole;
import com.view.dao.mapper.SysRoleMapper;
import com.view.dao.mapper.SysUserMapper;
import com.view.dao.mapper.SysUserRoleMapper;
import com.view.dto.UserLoginDTO;
import com.view.dto.UserRegisterDTO;
import com.view.enums.BusinessStatusEnum;
import com.view.enums.RoleEnum;
import com.view.exception.ServiceException;
import com.view.service.AuthService;
import com.view.service.PermissionService;
import com.view.service.SysMenuService;
import com.view.utils.BeanCopyUtils;
import com.view.vo.UserLoginVO;
import com.view.vo.menu.AsyncRoutesVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.view.enums.StatusCodeEnum.*;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-09 10:40
 * @description: 账号相关实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper userMapper;

    private final SysUserRoleMapper userRoleMapper;

    private final SysMenuService menuService;

    private final SysRoleMapper roleMapper;

    private final PermissionService permissionService;


    @Override
    public void register(UserRegisterDTO userRegisterDTO) {

//        SysUser exist = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getEmail, userRegisterDTO.getEmail()));
//        if (Objects.nonNull(exist)){
//            throw new ServiceException(USER_EXIST);
//        }
//        String password = SaSecureUtil.md5(userRegisterDTO.getPassword());
//
//        SysUser sysUser = SysUser.builder()
//                .email(userRegisterDTO.getEmail())
//                .password(userRegisterDTO.getPassword())
//                .nickname(userRegisterDTO.getNickname()).build();

    }





    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserLoginVO login(UserLoginDTO userLoginDTO, boolean skipPasswordCheck) {
        SysUser sysUser = userMapper.selectOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, userLoginDTO.getUsername()).or().eq(SysUser::getEmail, userLoginDTO.getUsername()));

        // 判断用户是否存在
        if (Objects.isNull(sysUser)) {
            throw new ServiceException(USER_NOT_EXIST);
        }

        // 判断用户是否被禁用
        if (sysUser.getStatus().equals(BusinessStatusEnum.DISABLED.getValue())) {
            throw new ServiceException(USER_FREEZE);
        }

        // 密码校验
        if (!skipPasswordCheck){
            //todo 校验密码
            String password = SaSecureUtil.md5(userLoginDTO.getPassword());
            if (!sysUser.getPassword().equals(password)) {
                throw new ServiceException(LOGIN_ERROR);
            }
        }

        // 查询用户角色
        List<SysUserRole> userRoleList = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, sysUser.getId()));
        Set<Long> roleIdList = userRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toSet());

        // 查看所属角色是否被禁用
        List<SysRole> sysRoles = roleMapper.selectList(new LambdaQueryWrapper<SysRole>().in(SysRole::getId, roleIdList));

        ArrayList<String> roleCodeList = new ArrayList<>();
        boolean existDisable = false;
        boolean hasSuperAdmin = false;

        // 遍历角色
        for (SysRole role : sysRoles) {

            // 角色code
            roleCodeList.add(role.getRoleCode());

            // 判断是否有被禁用的角色
            if (role.getStatus() == BusinessStatusEnum.DISABLED.getValue()) {
                existDisable = true;
            }

            // 判断是否有超级管理员
            if (role.getId().equals(RoleEnum.SUPERADMIN.getId())) {
                hasSuperAdmin = true;
            }
        }

        // 如果有被禁用的角色，且没有超级管理员
        if (existDisable && !hasSuperAdmin) {
            throw new ServiceException(ROLE_FREEZE);
        }

        // 构建菜单树
        List<AsyncRoutesVO> asyncRoutesVOList = menuService.buildMenuTreeByRoles(roleIdList);


        // 创建一个 SaLoginModel 对象
        SaLoginModel model = new SaLoginModel();

        // 添加自定义字段
        Map<String, Object> payload = new HashMap<>();
        payload.put("source","wechat");
        payload.put("timestamp",System.currentTimeMillis());
        model.setExtraData(payload);


        // 验证成功后的登录处理
        StpUtil.login(sysUser.getId(),model);

        // 用户角色code与权限,用户名存入缓存
        SaSession currentSession = StpUtil.getTokenSession();
        currentSession.set(SaSession.USER, sysUser.getUsername());
        currentSession.set(SaSession.ROLE_LIST, roleCodeList);
        Set<String> menuPermissionByRoles = permissionService.getMenuPermissionByRoles(roleIdList);
        currentSession.set(SaSession.PERMISSION_LIST, menuPermissionByRoles);

        // 获取当前回话的token
        String token = StpUtil.getTokenInfo().getTokenValue();
        UserLoginVO userLoginVO = BeanCopyUtils.copyBean(sysUser, UserLoginVO.class);
        userLoginVO.setRoles(roleCodeList);
        userLoginVO.setAccessToken(token);
        userLoginVO.setAsyncRoutesVOList(asyncRoutesVOList);
        return userLoginVO;
    }

    ;
}
