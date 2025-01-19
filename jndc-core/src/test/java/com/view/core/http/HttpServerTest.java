package com.view.core.http;

import com.view.core.server.http.HttpServer;
import com.view.core.server.http.HttpServerConfiguration;
import com.view.core.utils.SSLContextGenerator;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class HttpServerTest {




    @Test
    public void http() {
        HttpServerConfiguration httpServerConfiguration = new HttpServerConfiguration();
        //设置数据读取回调
        httpServerConfiguration.setDataReadCallback((context, fullHttpRequest) -> {
            HttpMethod method = fullHttpRequest.method();
            //判断请求方法
            if (method.equals(HttpMethod.GET)) {
                log.info("get request");
            } else if (method.equals(HttpMethod.POST)) {
                log.info("post request");
            }

            sendResponse(context);

        });
        httpServerConfiguration.setFailCallback(e -> {
            log.error("http server start fail", e);
        });
        httpServerConfiguration.setPort(8888);
        HttpServer server = new HttpServer();
        server.start(httpServerConfiguration);



    }

    private void sendResponse(ChannelHandlerContext context) {
        // 构造响应内容
        ByteBuf content = Unpooled.copiedBuffer("{}", CharsetUtil.UTF_8);

        // 构造响应对象
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

        // 设置响应头信息
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());

        // 发送响应
        context.writeAndFlush(response);
    }


    @Test
    public void https() {
        HttpServerConfiguration httpServerConfiguration = new HttpServerConfiguration();
        httpServerConfiguration.setPort(8888);
        httpServerConfiguration.setSslContext(SSLContextGenerator.SSL_CONTEXT);
        //设置数据读取回调
        httpServerConfiguration.setDataReadCallback((context, fullHttpRequest) -> {
            sendResponse(context);

        });
        httpServerConfiguration.setFailCallback(e -> {
            log.error("http server start fail", e);
        });

        HttpServer server = new HttpServer();
        server.start(httpServerConfiguration);
    }
}
