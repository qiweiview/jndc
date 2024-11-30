package com.view.jndc.server.utils;

import com.view.jndc.server.model.admin.AdminUserEntity;
import com.view.jndc.server.model.admin.Meta;
import com.view.jndc.server.model.admin.PermissionConfig;
import com.view.jndc.server.model.admin.PureRoute;

import java.util.Arrays;
import java.util.List;

public class PureHelper {

    public static PermissionConfig permissionConfig;
    public static AdminUserEntity user;

    static {

        user = new AdminUserEntity();

        // 使用 set 方法逐个设置字段
        user.setAvatar("https://avatars.githubusercontent.com/u/52823142"); // 设置 avatar
        user.setUsername("common");                                      // 设置 username
        user.setNickname("小林");                                          // 设置 nickname

        List<String> roles = Arrays.asList("common");                     // 设置 roles
        user.setRoles(roles);

        List<String> permissions = Arrays.asList("permission:btn:add", "permission:btn:edit"); // 设置 permissions
        user.setPermissions(permissions);

        user.setAccessToken("eyJhbGciOiJIUzUxMiJ9.common");            // 设置 accessToken
        user.setRefreshToken("eyJhbGciOiJIUzUxMiJ9.commonRefresh");   // 设置 refreshToken
        user.setExpires("2030/10/30 00:00:00");


        // Create Meta objects
        Meta permissionMeta = new Meta();
        permissionMeta.setTitle("权限管理");
        permissionMeta.setIcon("ep:lollipop");
        permissionMeta.setRank(10);

        Meta pageMeta = new Meta();
        pageMeta.setTitle("页面权限");
        pageMeta.setRoles(Arrays.asList("admin", "common"));

        Meta buttonMeta = new Meta();
        buttonMeta.setTitle("按钮权限");
        buttonMeta.setRoles(Arrays.asList("admin", "common"));

        Meta routerMeta = new Meta();
        routerMeta.setTitle("路由返回按钮权限");
        routerMeta.setAuths(Arrays.asList("permission:btn:add", "permission:btn:edit", "permission:btn:delete"));

        Meta loginMeta = new Meta();
        loginMeta.setTitle("登录接口返回按钮权限");

        // Create Route objects
        PureRoute permissionPagePureRoute = new PureRoute();
        permissionPagePureRoute.setPath("/permission/page/index");
        permissionPagePureRoute.setName("PermissionPage");
        permissionPagePureRoute.setMeta(pageMeta);

        PureRoute permissionButtonRouterPureRoute = new PureRoute();
        permissionButtonRouterPureRoute.setPath("/permission/button/router");
        permissionButtonRouterPureRoute.setName("PermissionButtonRouter");
        permissionButtonRouterPureRoute.setMeta(routerMeta);

        PureRoute permissionButtonLoginPureRoute = new PureRoute();
        permissionButtonLoginPureRoute.setPath("/permission/button/login");
        permissionButtonLoginPureRoute.setMeta(loginMeta);

        // Create children for the "按钮权限" route
        PureRoute permissionButtonPureRoute = new PureRoute();
        permissionButtonPureRoute.setPath("/permission/button");
        permissionButtonPureRoute.setMeta(buttonMeta);
        permissionButtonPureRoute.setChildren(Arrays.asList(permissionButtonRouterPureRoute, permissionButtonLoginPureRoute));

        // Create the root PermissionConfig
        permissionConfig = new PermissionConfig();
        permissionConfig.setPath("/permission");
        permissionConfig.setMeta(permissionMeta);
        permissionConfig.setChildren(Arrays.asList(permissionPagePureRoute, permissionButtonPureRoute));

    }
}
