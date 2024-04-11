package com.view.core.server;

import com.view.core.server.ndc.NDCServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NDCServerTest {

    private NDCServer server;

    @BeforeEach
    public void init() {
        server = new NDCServer();
    }

    @Test
    public void runServer() {
        server.start(10886);
    }
}
