package com.view.core.tcp;

import com.view.core.component.SupportEnvironment;
import com.view.core.server.tcp.TCPServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

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
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tcpServer.stop();
            log.info("执行关闭");
        }).start();

        tcpServer.start(888, () -> {
            log.info("15秒后关闭");
        });
    }


}
