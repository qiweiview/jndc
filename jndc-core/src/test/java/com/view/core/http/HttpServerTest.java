package com.view.core.http;

import com.view.core.server.http.HttpServer;
import com.view.core.utils.SSLContextGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HttpServerTest {

    private HttpServer server;

    @BeforeEach
    public void init() {
        server = new HttpServer();
    }

    @Test
    public void http() {
        server.start(80);
    }


    @Test
    public void https() {
        server.setSslContext(SSLContextGenerator.generateSslContextAuto());
        server.start(443);
    }
}
