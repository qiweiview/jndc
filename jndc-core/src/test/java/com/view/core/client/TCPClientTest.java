package com.view.core.client;

import com.view.core.client.tcp.TCPClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TCPClientTest {

    private TCPClient client;

    @BeforeEach
    public void init() {
        client = new TCPClient();
    }

    @Test
    public void runClient() {
        new Thread(() -> {
            client.write("hello im client".getBytes());
        }).start();

        client.start("127.0.0.1", 888);


        //写入数据

    }
}
