package com.view.jndc.core.v2.server;

import com.view.jndc.core.v2.componet.server.JNDCServer;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


@Slf4j
public class ServeTest {


    @Test
    public void start() throws InterruptedException {
        int port = 777;


        //服务端
        JNDCServer jndcServer = new JNDCServer();
        jndcServer.start(port);
        Thread currentThread = Thread.currentThread();



        synchronized (currentThread) {
            log.info("阻塞等待中");
            currentThread.wait();
        }

    }
}
