package com.view.core.client;

import com.view.core.client.http.HttpClient;
import org.junit.jupiter.api.Test;

public class ClientTest {

    @Test
    public void runClient() {
        HttpClient httpClient = new HttpClient();
//        httpClient.start("http://qw607.com");
        httpClient.start("http://127.0.0.1:10886/websocket");
    }
}
