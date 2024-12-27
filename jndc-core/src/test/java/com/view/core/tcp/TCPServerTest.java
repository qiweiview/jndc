package com.view.core.tcp;

import com.view.core.server.tcp.TCPServer;
import com.view.core.server.tcp.TCPServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j

public class TCPServerTest {

    private TCPServer tcpServer;


    @BeforeEach
    public void init() {

        tcpServer = new TCPServer();
    }

    @Test
    public void runServer() {
        TCPServerConfiguration tcpServerConfiguration = new TCPServerConfiguration();
        tcpServerConfiguration.setPort(8080);

        tcpServer.start(tcpServerConfiguration);
    }


}
