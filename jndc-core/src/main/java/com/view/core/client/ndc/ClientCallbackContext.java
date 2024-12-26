package com.view.core.client.ndc;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class ClientCallbackContext {
    private ChannelHandlerContext context;

    private NDCClient ndcClient;
}
