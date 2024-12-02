package com.view.jndc.server.config.aspect;


import com.view.jndc.server.model.admin.PureUserEntity;
import com.view.jndc.server.serivce.admin.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
@Order(1)//高于auth
public class RequiredLoginAspect {


    private final AdminService adminService;


    @Before("@annotation(RequiredLogin)")
    public void beforeMethodWithCustomAnnotation(JoinPoint joinPoint) {

        // 获取请求头信息
        HttpServletRequest request = getRequest();
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null) {
            throw new RuntimeException("凭证缺失");
        }

        //拉取用户信息
        PureUserEntity info = adminService.info(authorizationHeader);

        AuthContext.setAuthenticated(info);


    }

    private HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }

        // Handle the case where there is no current request
        throw new IllegalStateException("无法在当前请求上下文中找到HttpServletRequest");
    }
}
