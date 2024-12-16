package com.view.dto.account;

import com.view.enums.GenderEnum;
import com.view.validator.EnumValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-03 18:48
 * @description: 账号信息修改
 */
@Data
public class AccountUpdateDTO {
    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    private String email;

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
}
