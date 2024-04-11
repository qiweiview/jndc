package com.view.core.server.tcp;

import com.view.core.client.tcp.TCPClient;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class LocalProxyVirtualServer extends VirtualServer {
    private TCPClient tcpClient;


    @Override
    public void channelRead0(byte[] msg) {
        tcpClient.write(msg);
    }

    @Override
    public void channelInactive() {
    }

    @Override
    public void channelActive() {

    }
}
