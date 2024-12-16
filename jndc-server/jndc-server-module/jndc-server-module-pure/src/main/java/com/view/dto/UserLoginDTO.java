package com.view.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-07 16:42
 * @description: 后台邮箱登录参数
 */
@Data
public class UserLoginDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @Size(min = 6, max = 20, message = "密码长度必须在6到20个字符之间")
    private String password;
}
