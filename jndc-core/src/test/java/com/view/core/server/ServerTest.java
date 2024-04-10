package com.view.core.server;

import org.junit.jupiter.api.Test;

public class ServerTest {

    @Test
    public void runServer() {
        JNDCServer server = new JNDCServer();
        server.start(10886);
    }
}
