package com.view.core.server;

import com.view.core.server.http.HttpServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServerTest {

    private HttpServer server;

    @BeforeEach
    public void init() {
        server = new HttpServer();
    }

    @Test
    public void runServer() {

//        server.setSslContext(SSLContextGenerator.generateSslContextAuto());
        server.start(10886);
    }
}
