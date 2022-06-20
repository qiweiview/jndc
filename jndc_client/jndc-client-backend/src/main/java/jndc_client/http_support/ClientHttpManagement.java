package jndc_client.http_support;

import jndc.http_support.NettyHttpServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;

@Deprecated
@Slf4j
public class ClientHttpManagement {
//    public static ClientServiceDescription DEPLOY_PORT;

//    public static final String CLIENT_MANAGEMENT = "client_management";

    public void start() {
        NettyHttpServer nettyHttpServer = new NettyHttpServer();
        nettyHttpServer.setMappingSanPath("jndc_client.http_support");
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            int localPort = serverSocket.getLocalPort();
            serverSocket.close();
            log.info("management page---> http://localhost:" + localPort);

//            DEPLOY_PORT = new ClientServiceDescription();
//            DEPLOY_PORT.setServiceIp("127.0.0.1");
//            DEPLOY_PORT.setServicePort(localPort);
//            DEPLOY_PORT.setServiceName(CLIENT_MANAGEMENT);
//            DEPLOY_PORT.setServiceEnable(true);

            nettyHttpServer.start(localPort);
        } catch (IOException e) {
            throw new RuntimeException("get random port fail");
        }

    }


}
