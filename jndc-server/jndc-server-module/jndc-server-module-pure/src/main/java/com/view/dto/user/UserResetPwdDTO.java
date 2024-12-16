package com.view.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-01 17:33
 * @description: 密码重置
 */
@Data
public class UserResetPwdDTO {

    /**
     * ID
     */
    @NotNull(message = "用户编号不能为空")
    private Long id;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6到20个字符之间")
    private String password;

    private String idString;

    public void setId( Long id) {
        this.id = id;
        if (id != null&&idString==null) {
            this.idString = id.toString();
        }
    }

    public void setIdString(String idString) {
        this.idString = idString;
        if (idString != null) {
            this.id = Long.parseLong(idString);
        }
    }
}
