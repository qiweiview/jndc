package jndc.web_support.core;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLEngine;

@Slf4j
public class CustomSslHandler extends SslHandler {


    public static String NAME = "CUSTOM_SSL_HANDLER";


    public CustomSslHandler(SSLEngine engine) {
        super(engine);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("ssl error" + cause);
        ctx.close();
    }
}
