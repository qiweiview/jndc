package com.view.core.client;

import com.view.core.client.ndc.NDCClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NDCClientTest {

    private NDCClient ndcClient;

    @BeforeEach
    public void init() {
        ndcClient = new NDCClient();
    }

    @Test
    public void runClient() {
        ndcClient.start("127.0.0.1", 10886);
    }
}
