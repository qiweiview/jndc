package com.view.jndc.core.v2.client;

import com.view.jndc.core.v2.componet.client.JNDCClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;


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
        jndcClient.createWorkDirect();
        jndcClient.registerService("nacos", "192.168.0.102", 8848);


        synchronized (currentThread) {
            log.info("阻塞等待中");
            currentThread.wait();
        }

    }
}
