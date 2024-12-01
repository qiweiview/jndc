package com.view.jndc.server.admin;

import com.view.jndc.server.model.admin.PureMetaEntity;
import com.view.jndc.server.model.admin.PurePermissionEntity;
import com.view.jndc.server.model.admin.PureRouteEntity;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
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
        permissionButtonRouterPureRouteEntity.setMeta(routerPureMetaEntity);

        PureRouteEntity permissionButtonLoginPureRouteEntity = new PureRouteEntity();
        permissionButtonLoginPureRouteEntity.setPath("/permission/button/login");
        permissionButtonLoginPureRouteEntity.setMeta(loginPureMetaEntity);

        // Create children for the "按钮权限" route
        PureRouteEntity permissionButtonPureRouteEntity = new PureRouteEntity();
        permissionButtonPureRouteEntity.setPath("/permission/button");
        permissionButtonPureRouteEntity.setMeta(buttonPureMetaEntity);
        permissionButtonPureRouteEntity.setChildren(Arrays.asList(permissionButtonRouterPureRouteEntity, permissionButtonLoginPureRouteEntity));

        // Create the root PermissionConfig
        PurePermissionEntity purePermissionEntity = new PurePermissionEntity();
        purePermissionEntity.setPath("/permission");
        purePermissionEntity.setMeta(permissionPureMetaEntity);
        purePermissionEntity.setChildren(Arrays.asList(permissionPagePureRouteEntity, permissionButtonPureRouteEntity));

        // Print out the config for verification
        System.out.println(purePermissionEntity);
    }
}
