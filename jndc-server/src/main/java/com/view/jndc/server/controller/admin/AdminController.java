package com.view.jndc.server.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.jndc.server.config.aspect.RequiredLogin;
import com.view.jndc.server.model.EncryptedResponse;
import com.view.jndc.server.model.admin.PureUserEntity;
import com.view.jndc.server.serivce.admin.AdminService;
import com.view.jndc.server.utils.PureHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;


    @RequiredLogin
    @RequestMapping(value = "createUser", method = RequestMethod.POST)
    public EncryptedResponse createUser(@RequestBody PureUserEntity pureUserEntity) {
        int effect = adminService.createUser(pureUserEntity);
        return EncryptedResponse.success(effect);
    }

    @RequiredLogin
    @RequestMapping(value = "deleteUser", method = RequestMethod.POST)
    public EncryptedResponse deleteUser(@RequestBody PureUserEntity pureUserEntity) {
        int effect = adminService.deleteUser(pureUserEntity);
        return EncryptedResponse.success(effect);
    }


    @RequiredLogin
    @RequestMapping(value = "queryUserPage", method = RequestMethod.POST)
    public EncryptedResponse queryUserPage(@RequestBody PureUserEntity pureUserEntity) {
        Page<PureUserEntity> page = adminService.queryUserPage(pureUserEntity);
        return EncryptedResponse.success(page);
    }


    @RequestMapping(value = "login", method = RequestMethod.POST)
    public EncryptedResponse login(@RequestBody PureUserEntity pureUserEntity) {
        pureUserEntity = adminService.login(pureUserEntity);
        return EncryptedResponse.success(pureUserEntity);
    }

    @RequiredLogin
    @RequestMapping(value = "asyncRoutes", method = RequestMethod.GET)
    public EncryptedResponse asyncRoutes() {
        return EncryptedResponse.success(Arrays.asList(PureHelper.purePermissionEntity));
    }


}
