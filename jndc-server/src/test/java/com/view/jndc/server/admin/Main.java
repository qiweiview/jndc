package com.view.jndc.server.admin;

import com.view.jndc.server.model.admin.Meta;
import com.view.jndc.server.model.admin.PermissionConfig;
import com.view.jndc.server.model.admin.PureRoute;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
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
        PermissionConfig permissionConfig = new PermissionConfig();
        permissionConfig.setPath("/permission");
        permissionConfig.setMeta(permissionMeta);
        permissionConfig.setChildren(Arrays.asList(permissionPagePureRoute, permissionButtonPureRoute));

        // Print out the config for verification
        System.out.println(permissionConfig);
    }
}
