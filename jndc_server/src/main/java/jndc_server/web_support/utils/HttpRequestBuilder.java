package jndc_server.web_support.utils;


import io.netty.handler.codec.http.*;

public class HttpRequestBuilder {
    public static FullHttpRequest simpleGet(String uri) {
        FullHttpRequest fullHttpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri);

        return fullHttpRequest;
    }
}
