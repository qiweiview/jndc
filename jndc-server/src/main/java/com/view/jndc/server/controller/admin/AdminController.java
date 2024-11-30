package com.view.jndc.server.controller.admin;

import com.view.jndc.server.model.EncryptedResponse;
import com.view.jndc.server.utils.PureHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public EncryptedResponse listNDCClient() {
        return EncryptedResponse.success(PureHelper.user);
    }

    @RequestMapping(value = "asyncRoutes", method = RequestMethod.GET)
    public EncryptedResponse asyncRoutes() {
        return EncryptedResponse.success(Arrays.asList(PureHelper.purePermissionEntity));
    }

    @RequestMapping(value = "fail", method = RequestMethod.GET)
    public EncryptedResponse fail() {
        return EncryptedResponse.failed("failii");
    }
}
