package com.view.core.tcp;

import com.view.core.client.tcp.TCPClient;
import com.view.core.server.tcp.TCPServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TCPServerTest {

    private TCPServer server;

    @BeforeEach
    public void init() {
        server = new TCPServer();
    }

    @Test
    public void runServer() {
        server.start(888);
    }

    @Test
    public void runLocalProxyServer() {
        TCPClient tcpClient = new TCPClient();
        tcpClient.start("qw607.com", 80);


        server.start(888);
    }
}
