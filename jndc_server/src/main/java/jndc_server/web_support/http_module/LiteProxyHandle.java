package jndc_server.web_support.http_module;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import jndc.core.UniqueBeanManage;
import jndc_server.web_support.model.data_object.HttpHostRoute;
import jndc_server.web_support.utils.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LiteProxyHandle extends SimpleChannelInboundHandler<FullHttpResponse> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static String NAME = "LITE_PROXY_HANDLE";

    private LiteHttpProxy liteHttpProxy;

    public LiteProxyHandle(LiteHttpProxy liteHttpProxy) {
        this.liteHttpProxy = liteHttpProxy;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpResponse fullHttpResponse) throws Exception {
        liteHttpProxy.writeData(fullHttpResponse);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("forward point inactive");
        liteHttpProxy.release();
    }
}
