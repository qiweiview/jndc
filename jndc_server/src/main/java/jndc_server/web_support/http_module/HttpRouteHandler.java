package jndc_server.web_support.http_module;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.AsciiString;
import jndc.core.UniqueBeanManage;
import jndc_server.core.JNDCServerConfig;
import jndc_server.web_support.core.JNDCHttpRequest;
import jndc_server.web_support.model.data_object.HttpHostRoute;
import jndc_server.web_support.utils.HttpResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class HttpRouteHandler extends SimpleChannelInboundHandler<JNDCHttpRequest> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    public static String NAME = "HTTP_ROUTE_HANDLER";


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, JNDCHttpRequest jndcHttpRequest) throws Exception {
        HostRouter hostRouter = UniqueBeanManage.getBean(HostRouter.class);
        String host = jndcHttpRequest.getStringHeader(AsciiString.of("host"));
        HttpHostRoute httpHostRoute = hostRouter.matchHost(host);
        FullHttpResponse fullHttpResponse;
        if (httpHostRoute != null) {
            if (httpHostRoute.returnFixedValue()) {
                fullHttpResponse = HttpResponseBuilder.defaultResponse(httpHostRoute.getFixedResponse().getBytes(), httpHostRoute.getFixedContentType() + ";charset=utf-8");
            } else {
                fullHttpResponse = HttpResponseBuilder.redirectResponse(httpHostRoute.getRedirectAddress());
            }
        } else {
            fullHttpResponse = HttpResponseBuilder.textResponse("不存该路径匹配规则".getBytes());
        }

        channelHandlerContext.writeAndFlush(fullHttpResponse);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("unCatchableException: " + cause);

    }
}
