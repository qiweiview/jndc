package jndc_client.http_support;

import jndc.http_support.NettyHttpServer;
import jndc_client.core.ClientServiceDescription;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;

@Slf4j
public class ClientHttpManagement {
    public static ClientServiceDescription DEPLOY_PORT;

    public static final String CLIENT_MANAGEMENT = "client_management";

    public void start() {
        NettyHttpServer nettyHttpServer = new NettyHttpServer();
        nettyHttpServer.setMappingSanPath("jndc_client.http_support");
        try {
            ServerSocket s = new ServerSocket(0);
            int localPort = s.getLocalPort();
            s.close();
            log.info("management page---> http://localhost:" + localPort);


            DEPLOY_PORT = new ClientServiceDescription();
            DEPLOY_PORT.setServiceIp("127.0.0.1");
            DEPLOY_PORT.setServicePort(localPort);
            DEPLOY_PORT.setServiceName(CLIENT_MANAGEMENT);
            DEPLOY_PORT.setServiceEnable(true);


            nettyHttpServer.start(s.getLocalPort());
        } catch (IOException e) {
            throw new RuntimeException("get random port fail");
        }

    }


}
