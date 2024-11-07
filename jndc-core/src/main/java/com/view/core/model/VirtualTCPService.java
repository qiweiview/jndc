package com.view.core.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class VirtualTCPService implements Serializable {
    //由上下文填写
    private String ndcClientId;

    private int expectPort;

    private String serviceId;

    private String description;

    private String host;

    private int port;
}
