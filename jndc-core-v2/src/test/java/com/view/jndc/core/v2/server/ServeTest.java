package com.view.jndc.core.v2.server;

import com.view.jndc.core.v2.componet.client.JNDCClient;
import com.view.jndc.core.v2.componet.server.JNDCServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


@Slf4j
public class ServeTest {


    @Test
    public void start() throws InterruptedException {
        int port = 777;


        //服务端
        JNDCServer jndcServer = new JNDCServer();
        jndcServer.start(port);
        Thread currentThread = Thread.currentThread();

        TimeUnit.SECONDS.sleep(1);

        //客户端
        JNDCClient jndcClient = new JNDCClient();
        jndcClient.start("127.0.0.1", port);
        Stream.generate(() -> UUID.randomUUID()).limit(1).forEach(x -> {
            jndcClient.openChannel();
        });
//        jndcClient.testBandwidth(10,TimeUnit.SECONDS);


        synchronized (currentThread) {
            log.info("阻塞等待中");
            currentThread.wait();
        }

    }
}
