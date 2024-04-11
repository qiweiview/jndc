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
    public void httpRequest() {
        httpClient.start("http://qw607.com");
    }

    @Test
    public void websocketRequest() {
        httpClient.start("ws://127.0.0.1:10886/websocket");
    }


}
