package com.view.core.server.http;

import com.view.core.model.CheckAbleConfiguration;
import com.view.core.protocol.NDCPacket;
import com.view.core.server.ndc.ServerCallbackContext;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.ssl.SslContext;
import lombok.Data;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
public class HttpServerConfiguration  extends CheckAbleConfiguration {
    private Runnable startCallBack=EMPTY_CALLBACK;

    private BiConsumer<ChannelHandlerContext, FullHttpRequest> dataReadCallback = EMPTY_BICONSUMER(ChannelHandlerContext.class, FullHttpRequest.class);

    private Consumer<Exception> failCallback = EMPTY_CONSUMER(Exception.class);

    private Runnable stopCallback = EMPTY_CALLBACK;

    private SslContext sslContext;

    private String  host="127.0.0.1";

    private int port=80;

    @Override
    public void check() {

    }
}
