package com.view.core.server;

import com.view.core.server.ndc.NDCServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NDCServerTest {

    private NDCServer ndcServer;

    @BeforeEach
    public void init() {
        ndcServer = new NDCServer();
    }

    @Test
    public void runServer() {
        ndcServer.start(10886);
    }
}
