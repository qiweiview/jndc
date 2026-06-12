package jndc.web_support.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;

public class HttpResponseBuilder {
    private static final byte[] notFoundModel = "<html><body>file not be found</body><html>".getBytes();

    private static final String TEXT_PLAIN = "text/plain;charset=utf-8";

    private static final String APPLICATION_JSON = "application/json;charset=utf-8";

    private static final String HTML = "text/html;charset=utf-8";

    private static final String JS = "application/javascript;charset=utf-8";

    private static final String CSS = "text/css;charset=utf-8";


    public static FullHttpResponse fileResponse(byte[] bytes, String fileType) {
        String contentType;
        String cacheControl;
        
        // Set content type and cache control based on file type
        if ("html".equalsIgnoreCase(fileType)) {
            contentType = HTML;
            cacheControl = "no-cache"; // HTML files should not be cached
        } else if ("js".equalsIgnoreCase(fileType)) {
            contentType = JS;
            cacheControl = "public, max-age=31536000"; // Cache JS files for 1 year
        } else if ("css".equalsIgnoreCase(fileType)) {
            contentType = CSS;
            cacheControl = "public, max-age=31536000"; // Cache CSS files for 1 year
        } else if ("png".equalsIgnoreCase(fileType) || "jpg".equalsIgnoreCase(fileType) || 
                  "jpeg".equalsIgnoreCase(fileType) || "gif".equalsIgnoreCase(fileType) ||
                  "ico".equalsIgnoreCase(fileType) || "svg".equalsIgnoreCase(fileType)) {
            contentType = "image/" + fileType.toLowerCase();
            cacheControl = "public, max-age=31536000"; // Cache images for 1 year
        } else {
            contentType = TEXT_PLAIN;
            cacheControl = "public, max-age=7200"; // Default cache for 2 hours
        }

        FullHttpResponse defaultHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        ByteBuf content = defaultHttpResponse.content();
        content.writeBytes(bytes);
        HttpHeaders headers = defaultHttpResponse.headers();
        headers.set(HttpHeaderNames.CONTENT_TYPE, contentType);
        headers.set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        headers.set(HttpHeaderNames.CACHE_CONTROL, cacheControl);
        
        // Add ETag
        String etag = "\"" + Integer.toHexString(bytes.hashCode()) + "\"";
        headers.set(HttpHeaderNames.ETAG, etag);
        
        // Add Vary header to ensure proper caching behavior
        headers.set(HttpHeaderNames.VARY, "Accept-Encoding");

        return defaultHttpResponse;
    }

    public static FullHttpResponse textResponse(byte[] bytes) {
        return defaultResponse(bytes, TEXT_PLAIN);
    }

    public static FullHttpResponse htmlResponse(byte[] bytes) {
        return defaultResponse(bytes, HTML);
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

    public static FullHttpResponse redirectResponse(String newLocation) {
        ByteBuf emptyBuffer = Unpooled.EMPTY_BUFFER;
        FullHttpResponse defaultHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND, emptyBuffer);
        HttpHeaders headers = defaultHttpResponse.headers();
        headers.set(HttpHeaderNames.LOCATION, newLocation);
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
