package com.view.core.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Data
@Slf4j
public class TCPDataTransport implements Serializable {
    private static final long serialVersionUID = 5613575214075644978L;

    //远程
    private String ndcServerId;

    private String appServerId;

    private String appServerSessionId;

    //本地
    private String ndcClientId;

    private String clientServiceId;

    private String clientServiceSessionId;

    private byte[] data;


}
