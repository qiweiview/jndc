package com.view.core.tcp;

import com.view.core.server.tcp.TCPServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@Slf4j

public class TCPServerTest {

    private TCPServer server;

    @BeforeEach
    public void init() {
        server = new TCPServer();
    }

    @Test
    public void runServer() {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            server.stop();
            log.info("执行关闭");
        }).start();

        server.start(888, () -> {
            log.info("15秒后关闭");
        });
    }


}
