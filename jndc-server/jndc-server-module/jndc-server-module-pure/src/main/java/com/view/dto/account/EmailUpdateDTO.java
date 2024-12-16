package com.view.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-04 0:14
 * @description: 邮箱换绑
 */
@Data
public class EmailUpdateDTO {

    @NotBlank(message = "邮箱号不能为空")
    @Email(message = "邮箱号格式错误")
    private String newEmail;

    @NotBlank(message = "验证码不能为空")
    @Pattern(regexp="^\\d{6}$",message="验证码格式不正确！")
    private String verificationCode;
}
