package com.view.jndc.server.model.admin;

import lombok.Data;

import java.util.List;

@Data
public class PureRoute {
    private String path;

    private String name;

    private Meta meta;

    private List<PureRoute> children;  // Child routes

}

