package com.view.dto.user;

import com.view.model.dto.BasePageDTO;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-11 11:38
 * @description: 用户查询对象
 */
@Data
public class UserQueryDTO extends BasePageDTO {

    private String username;

    private String email;

    private String phone;

    private String nickname;

    private Integer status;
}
