package jndc_client.http_support;

import jndc.http_support.NettyHttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
public class ClientHttpManagement {
    public void start() {
        NettyHttpServer nettyHttpServer = new NettyHttpServer();
        nettyHttpServer.setMappingSanPath("jndc_client.http_support");
        try {
            ServerSocket s = new ServerSocket(0);
            int localPort = s.getLocalPort();
            s.close();
            log.info("management page---> http://localhost:" + localPort);
            nettyHttpServer.start(s.getLocalPort());
        } catch (IOException e) {
            throw new RuntimeException("get random port fail");
        }

    }


}
