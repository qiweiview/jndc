package com.view.core.server.ndc;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;

@Data
@Slf4j
public class ServerCallbackContext {

    private ChannelHandlerContext context;

    private NDCServer ndcServer;
}
