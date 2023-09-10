package com.view.jndc.core.v2.server;

import com.view.jndc.core.v2.componet.server.JNDCServer;
import com.view.jndc.core.v2.componet.server.ServiceProxy;
import com.view.jndc.core.v2.model.json_object.ServiceRegister;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;


@Slf4j
public class ServeTest {


    @Test
    public void startPortProtector() throws InterruptedException {

        ServiceRegister nacos = ServiceRegister.of("nacos", "192.168.0.102", 8848);


        ServiceProxy serviceProxy = new ServiceProxy();
        serviceProxy.start(888);
        serviceProxy.proxyTo(nacos);
        Thread currentThread = Thread.currentThread();


        synchronized (currentThread) {
            log.info("阻塞等待中");
            currentThread.wait();
        }
    }

    @Test
    public void startJNDCServer() throws InterruptedException {
        int port = 777;


        //服务端
        JNDCServer jndcServer = new JNDCServer();
        jndcServer.start(port);
        jndcServer.createWorkDirect();
        Thread currentThread = Thread.currentThread();


        synchronized (currentThread) {
            log.info("阻塞等待中");
            currentThread.wait();
        }

    }
}
