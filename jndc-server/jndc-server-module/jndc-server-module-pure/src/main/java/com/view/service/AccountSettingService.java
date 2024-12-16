package com.view.service;

import com.view.dto.account.EmailUpdateDTO;
import com.view.dto.account.PasswordUpdateDTO;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-03 22:31
 * @description: 用户账号
 */
public interface AccountSettingService {

    /**
     * 发送验证码，修改密码或邮箱用
     * @param email 为空则是修改密码
     */

    void sendCode(String email);

    /**
     * 修改密码
     */
    void updatePassword(PasswordUpdateDTO updateDTO);

    /**
     * 邮箱号
     */
    void updateEmail(EmailUpdateDTO updateDTO);
}
