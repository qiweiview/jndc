package com.view.core.client;

import com.view.core.client.ndc.NDCClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NDCClientTest {

    private NDCClient client;

    @BeforeEach
    public void init() {
        client = new NDCClient();
    }

    @Test
    public void runClient() {


        client.start("127.0.0.1", 10886);


    }
}
