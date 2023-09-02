package com.view.jndc.core.v2.client;

import com.view.jndc.core.v2.componet.client.JNDCClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


@Slf4j
public class ClientTest {


    @Test
    public void start() throws InterruptedException {
        int port = 777;

        Thread currentThread = Thread.currentThread();

        TimeUnit.SECONDS.sleep(1);

        //客户端
        JNDCClient jndcClient = new JNDCClient();
        jndcClient.start("127.0.0.1", port);

        Stream.generate(() -> UUID.randomUUID()).limit(200).forEach(x -> {
            jndcClient.openChannel();
        });


        synchronized (currentThread) {
            log.info("阻塞等待中");
            currentThread.wait();
        }

    }
}
