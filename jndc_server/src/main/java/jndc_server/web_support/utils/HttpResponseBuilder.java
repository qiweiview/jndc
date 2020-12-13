package jndc_server.web_support.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

public class HttpResponseBuilder {
    private static final byte[] notFoundModel="<html><body>file not be found,maybe you want go to <a href=\"./index.html\">home page</a></body><html>".getBytes();

    private static final String TEXT_PLAIN = "text/plain";

    private static final String APPLICATION_JSON = "application/json";

    private static final String HTML = "text/html";

    private static final String JS = "application/javascript";

    private static final String CSS = "text/css";


    public static FullHttpResponse fileResponse(byte[] bytes, String fileType) {
        String contentType;
        if ("html".equalsIgnoreCase(fileType)) {
            contentType = HTML;
        } else if ("js".equalsIgnoreCase(fileType)) {
            contentType = JS;
        } else if ("css".equalsIgnoreCase(fileType)) {
            contentType = CSS;
        } else {
            contentType = TEXT_PLAIN;
        }
        FullHttpResponse defaultHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        ByteBuf content = defaultHttpResponse.content();
        content.writeBytes(bytes);
        HttpHeaders headers = defaultHttpResponse.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        headers.set(HttpHeaderNames.CACHE_CONTROL, "public, max-age=7200");

        return defaultHttpResponse;
    }

    public static FullHttpResponse textResponse(byte[] bytes) {
        return defaultResponse(bytes, TEXT_PLAIN);
    }

    public static FullHttpResponse jsonResponse(byte[] bytes) {
        return defaultResponse(bytes, APPLICATION_JSON);
    }


    public static FullHttpResponse notFoundResponse() {
        ByteBuf byteBuf = Unpooled.copiedBuffer(notFoundModel);
        FullHttpResponse defaultHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND, byteBuf);
        HttpHeaders headers = defaultHttpResponse.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE, HTML);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
        return defaultHttpResponse;
    }

    public static FullHttpResponse emptyResponse() {
        ByteBuf emptyBuffer = Unpooled.EMPTY_BUFFER;
        FullHttpResponse defaultHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, emptyBuffer);
        HttpHeaders headers = defaultHttpResponse.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE, TEXT_PLAIN);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, emptyBuffer.readableBytes());
        return defaultHttpResponse;
    }

    public static FullHttpResponse defaultResponse(byte[] bytes, String contentType) {
        FullHttpResponse defaultHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        ByteBuf content = defaultHttpResponse.content();
        content.writeBytes(bytes);
        HttpHeaders headers = defaultHttpResponse.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        return defaultHttpResponse;
    }
}
