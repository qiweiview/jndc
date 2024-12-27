package com.view.core.server.ndc;

import com.view.core.model.CheckAbleConfiguration;
import com.view.core.protocol.NDCPacket;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Data
public class NDCServerConfiguration extends CheckAbleConfiguration {
    private String uniqueId;

    private String host;

    private int port;

    /*------服务端本身------*/
    private Runnable startedCallback = EMPTY_CALLBACK;

    private Runnable stopCallback = EMPTY_CALLBACK;

    private Consumer<Exception> failCallback = EMPTY_FAIL_CALLBACK;

    /*------服务端通讯------*/
    private Consumer<ChannelHandlerContext> connectActiveCallback = EMPTY_CONSUMER(ChannelHandlerContext.class);

    private BiConsumer<NDCPacket, ServerCallbackContext> dataReadCallback = EMPTY_BICONSUMER(NDCPacket.class, ServerCallbackContext.class);

    private Consumer<ServerCallbackContext> connectInActiveCallback = EMPTY_CONSUMER(ServerCallbackContext.class);


    @Override
    public void check() {
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("host不能为空");
        }
        if (port <= 0) {
            throw new IllegalArgumentException("port必须大于0");
        }

        if (uniqueId == null || uniqueId.isEmpty()) {
            throw new IllegalArgumentException("uniqueId不能为空");
        }


    }
}
