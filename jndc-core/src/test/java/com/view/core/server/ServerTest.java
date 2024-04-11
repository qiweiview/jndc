package com.view.core.server;

import com.view.core.server.http.HttpServer;
import org.junit.jupiter.api.Test;

public class ServerTest {

    @Test
    public void runServer() {
        HttpServer server = new HttpServer();
//        server.setSslContext(SSLContextGenerator.generateSslContextAuto());
        server.start(10886);
    }
}
