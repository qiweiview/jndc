package com.view.core.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class VirtualService implements Serializable {

    private String belongClient;

    private int expectPort;

    private String serviceId;

    private String description;

    private String host;

    private int port;
}
