package com.view.jndc.core.v2.server;

import com.view.jndc.core.v2.componet.client.JNDCClient;
import com.view.jndc.core.v2.componet.server.JNDCServer;
import com.view.jndc.core.v2.model.jndc.JNDCData;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class ServeTest {
    private static final Logger log = LoggerFactory.getLogger(ServeTest.class);


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
        jndcClient.write(JNDCData.SAY_HI_WORLD);

        synchronized (currentThread) {
            log.info("阻塞等待中");
            currentThread.wait();
        }

    }
}
