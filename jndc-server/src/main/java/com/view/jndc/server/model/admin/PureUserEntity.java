package com.view.jndc.server.model.admin;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.server.model.TraceableEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@TableName("pure_user")
public class PureUserEntity extends TraceableEntity {

    @TableField(value = "avatar")
    private String avatar;         // User avatar URL

    @TableField(value = "username")
    private String username;       // Username

    @TableField(value = "password")
    private String password;       // Username

    @TableField(value = "nickname")
    private String nickname;       // Nickname

    @TableField(value = "roles")
    private String roleString;

    @TableField(exist = false)
    private List<String> roles;    // List of user roles

    @TableField(value = "permissions")
    private String permissionString;

    @TableField(exist = false)
    private List<String> permissions;  // List of user permissions

    @TableField(value = "access_token")
    private String accessToken;    // JWT access token

    @TableField(value = "refresh_token")
    private String refreshToken;   // JWT refresh token

    @TableField(value = "expires")
    private String expires;        // Expiration time for the tokenprivate String nickname;       // Nickname


    public static String ddl() {
        return "create table if not exists pure_user (\n" +
                "    id bigint auto_increment primary key,\n" +
                "    avatar varchar(255),\n" +
                "    username varchar(255),\n" +
                "    password varchar(255),\n" +
                "    nickname varchar(255),\n" +
                "    roles varchar(255),\n" +
                "    permissions varchar(255),\n" +
                "    access_token varchar(255),\n" +
                "    refresh_token varchar(255),\n" +
                "    expires varchar(255),\n" +
                "    create_date datetime,\n" +
                "    update_date datetime\n" +
                ");";
    }

    public PureUserEntity desensitization() {
        this.password = null;
        return this;
    }
}
