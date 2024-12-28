package com.view.core.model;

import com.view.core.model.local_service.RegisterResponse;
import com.view.core.model.tcp_data.TCPResponse;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.InetSocketAddress;

@Data
@Slf4j
public class TCPDataTransport implements Serializable {
    private static final long serialVersionUID = 5613575214075644978L;

    private String ndcClientId;

    private String serviceId;

    private String tcpChannelId;

    private byte[] data;

    private InetSocketAddress remote;

    private TCPResponse tcpResponse;


    public boolean isSuccessful() {
        return TCPResponse.SUCCESS.equals(tcpResponse);
    }

    public boolean isServiceNotExist() {
        return TCPResponse.SERVICE_NOT_EXIST.equals(tcpResponse);
    }

    public boolean isRemoteConnectionInterrupt() {
        return TCPResponse.REMOTE_CONNECTION_INTERRUPT.equals(tcpResponse);
    }
}
