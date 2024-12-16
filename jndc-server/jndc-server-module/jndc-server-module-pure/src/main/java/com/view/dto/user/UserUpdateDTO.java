package com.view.dto.user;

import com.view.enums.BusinessStatusEnum;
import com.view.enums.GenderEnum;
import com.view.validator.EnumValue;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-12 15:09
 * @description: 用户修改对象
 */
@Data
public class UserUpdateDTO {

    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 用户名（登录名）
     */
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{4,16}$", message = "用户名必须为4至16位的字母、数字、下划线组成")
    private String username;

    /**
     * 邮箱号
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 简介
     */
    @Size(max = 300, message = "简介长度不能超过300个字符")
    private String intro;

    /**
     * 性别（0未知，1男，2女）
     */
    @NotNull(message = "性别不能为空")
    @EnumValue(enumClass = GenderEnum.class, message = "性别只能为0（未知），1（男），2（女）")
    private Integer gender;

    /**
     * 出生日期
     */
    @Past(message = "出生日期必须是过去的时间")
    private LocalDateTime birthday;

    /**
     * 状态（0正常，1禁用）
     */
    @NotNull(message = "状态不能为空")
    @EnumValue(enumClass = BusinessStatusEnum.class, message = "状态只能为0（正常）或1（冻结）")
    private Integer status;

    private String idString;

    public void setId(Long id) {
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
