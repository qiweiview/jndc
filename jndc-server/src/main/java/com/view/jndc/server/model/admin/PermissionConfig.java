package com.view.jndc.server.model.admin;

import lombok.Data;

import java.util.List;

@Data
public class PermissionConfig {
    private String path;

    private Meta meta;

    private List<PureRoute> children;  // List of child routes

}
