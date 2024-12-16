package com.view.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.view.annotation.AdminPrefix;
import com.view.annotation.LoginLog;
import com.view.dto.UserLoginDTO;
import com.view.dto.UserRegisterDTO;
import com.view.model.vo.ResponseResult;
import com.view.service.AuthService;
import com.view.vo.UserLoginVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-07 16:10
 * @description: 后台登录控制层
 */
@AdminPrefix
@RequiredArgsConstructor
@Slf4j
public class AdminAuthController {

    private final AuthService authService;

    /**
     * 第一个参数不可改变位置
     */
    @LoginLog
    @PostMapping("/login")
    public ResponseResult<UserLoginVO> login(@Valid @RequestBody UserLoginDTO user){
        return ResponseResult.ok(authService.login(user, false));
    }

    @PostMapping("/register")
    public ResponseResult<Void> register( UserRegisterDTO user){
        // 未实现
        return ResponseResult.ok();
    }

    @PostMapping("/logout")
    public ResponseResult<Void> logout(){
        StpUtil.getTokenSession().clear();
        StpUtil.logout();
        return ResponseResult.ok();
    }
}
