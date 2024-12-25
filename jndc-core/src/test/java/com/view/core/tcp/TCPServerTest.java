package com.view.core.tcp;

import com.view.core.component.SupportEnvironment;
import com.view.core.server.tcp.TCPServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j

public class TCPServerTest {

    private TCPServer tcpServer;
    private SupportEnvironment supportEnvironment;

    @BeforeEach
    public void init() {
        supportEnvironment = new SupportEnvironment();
        tcpServer = new TCPServer(supportEnvironment);
    }

    @Test
    public void runServer() {


        tcpServer.start(888, () -> {
            log.info("服务启动成功");
        });
    }


}
