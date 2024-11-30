package com.view.jndc.server.model.admin;

import lombok.Data;

import java.util.List;

@Data
public class Meta {
    private String title;

    private String icon;

    private Integer rank;

    private List<String> roles;  // List of roles (e.g., "admin", "common")

    private List<String> auths;  // List of permissions (e.g., "permission:btn:add")

}
