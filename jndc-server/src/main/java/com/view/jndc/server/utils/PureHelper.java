package com.view.jndc.server.utils;

import com.view.jndc.server.model.admin.PureUserEntity;
import com.view.jndc.server.model.admin.PureMetaEntity;
import com.view.jndc.server.model.admin.PurePermissionEntity;
import com.view.jndc.server.model.admin.PureRouteEntity;

import java.util.Arrays;
import java.util.List;

public class PureHelper {

    public static PurePermissionEntity purePermissionEntity;
    public static PureUserEntity user;

    static {

        user = new PureUserEntity();

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
        PureMetaEntity permissionPureMetaEntity = new PureMetaEntity();
        permissionPureMetaEntity.setTitle("权限管理");
        permissionPureMetaEntity.setIcon("ep:lollipop");
        permissionPureMetaEntity.setRank(10);

        PureMetaEntity pagePureMetaEntity = new PureMetaEntity();
        pagePureMetaEntity.setTitle("页面权限");
        pagePureMetaEntity.setRoles(Arrays.asList("admin", "common"));

        PureMetaEntity buttonPureMetaEntity = new PureMetaEntity();
        buttonPureMetaEntity.setTitle("按钮权限");
        buttonPureMetaEntity.setRoles(Arrays.asList("admin", "common"));

        PureMetaEntity routerPureMetaEntity = new PureMetaEntity();
        routerPureMetaEntity.setTitle("路由返回按钮权限");
        routerPureMetaEntity.setAuths(Arrays.asList("permission:btn:add", "permission:btn:edit", "permission:btn:delete"));

        PureMetaEntity loginPureMetaEntity = new PureMetaEntity();
        loginPureMetaEntity.setTitle("登录接口返回按钮权限");

        // Create Route objects
        PureRouteEntity permissionPagePureRouteEntity = new PureRouteEntity();
        permissionPagePureRouteEntity.setPath("/permission/page/index");
        permissionPagePureRouteEntity.setName("PermissionPage");
        permissionPagePureRouteEntity.setMeta(pagePureMetaEntity);

        PureRouteEntity permissionButtonRouterPureRouteEntity = new PureRouteEntity();
        permissionButtonRouterPureRouteEntity.setPath("/permission/button/router");
        permissionButtonRouterPureRouteEntity.setName("PermissionButtonRouter");
        permissionButtonRouterPureRouteEntity.setComponent("permission/button/index");
        permissionButtonRouterPureRouteEntity.setMeta(routerPureMetaEntity);

        PureRouteEntity permissionButtonLoginPureRouteEntity = new PureRouteEntity();
        permissionButtonLoginPureRouteEntity.setPath("/permission/button/login");
        permissionButtonLoginPureRouteEntity.setName("PermissionButtonLogin");
        permissionButtonLoginPureRouteEntity.setComponent("permission/button/perms");
        permissionButtonLoginPureRouteEntity.setMeta(loginPureMetaEntity);

        // Create children for the "按钮权限" route
        PureRouteEntity permissionButtonPureRouteEntity = new PureRouteEntity();
        permissionButtonPureRouteEntity.setPath("/permission/button");
        permissionButtonPureRouteEntity.setMeta(buttonPureMetaEntity);
        permissionButtonPureRouteEntity.setChildren(Arrays.asList(permissionButtonRouterPureRouteEntity, permissionButtonLoginPureRouteEntity));

        // Create the root PermissionConfig
        purePermissionEntity = new PurePermissionEntity();
        purePermissionEntity.setPath("/permission");
        purePermissionEntity.setMeta(permissionPureMetaEntity);
        purePermissionEntity.setChildren(Arrays.asList(permissionPagePureRouteEntity, permissionButtonPureRouteEntity));

    }
}
