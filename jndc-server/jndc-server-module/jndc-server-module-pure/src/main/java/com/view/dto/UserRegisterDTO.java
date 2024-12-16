package com.view.dto;

import com.view.validator.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-09 14:26
 * @description: 用户注册
 */
@Data
public class UserRegisterDTO {

    /**
     * 昵称
     */
    @Pattern(regexp="^.{2,15}$", message="昵称长度必须在2到15个字符之间")
    private String nickname;

    /**
     * 用户名/邮箱
     */
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 密码
     */
    @ValidPassword(message = "密码格式应为8-18位数字、字母、符号的任意两种组合")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp="^\\d{6}$",message="验证码格式不正确")
    private String verificationCode;

}
