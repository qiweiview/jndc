package com.view.core.client;

import com.view.core.client.http.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HttpClientTest {

    private HttpClient httpClient;

    @BeforeEach
    public void init() {
        httpClient = new HttpClient();
    }

    @Test
    public void http() {
        httpClient.start("http://127.0.0.1");
    }

    public void https() {
        httpClient.start("https://127.0.0.1");
    }

    @Test
    public void ws() {
        httpClient.start("ws://127.0.0.1/websocket");
    }

    @Test
    public void wss() {
        httpClient.start("wss://127.0.0.1/websocket");
    }


}
