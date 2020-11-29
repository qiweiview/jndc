package web.core;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.net.ssl.SSLEngine;

public class CustomSslHandler extends SslHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static String NAME = "CUSTOM_SSL_HANDLER";


    public CustomSslHandler(SSLEngine engine) {
        super(engine);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("ssl error" + cause);
        ctx.close();
    }
}
