package com.view.core.server.ndc;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ServerCallbackContext {
    private ChannelHandlerContext ctx;

    private NDCServer ndcServer;
}
