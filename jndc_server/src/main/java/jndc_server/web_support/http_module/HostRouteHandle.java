package jndc_server.web_support.http_module;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import jndc.core.UniqueBeanManage;
import jndc_server.config.ServerRuntimeConfig;
import jndc_server.web_support.model.data_object.HttpHostRoute;
import jndc_server.web_support.utils.HttpResponseBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HostRouteHandle extends SimpleChannelInboundHandler<FullHttpRequest> {
    public static String NAME = "HOST_ROUTE_HANDLE";


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {

        HttpHeaders headers = fullHttpRequest.headers();

        HostRouterComponent hostRouterComponent = UniqueBeanManage.getBean(HostRouterComponent.class);
        String host = headers.get(HttpHeaderNames.HOST);
        // host="blog.ab.com";
        HttpHostRoute httpHostRoute = hostRouterComponent.matchHost(host);
        FullHttpResponse fullHttpResponse = null;
        if (httpHostRoute != null) {


            if (httpHostRoute.fixValueType()) {
                //todo return fix value
                fullHttpResponse = HttpResponseBuilder.defaultResponse(httpHostRoute.getFixedResponse().getBytes(), httpHostRoute.getFixedContentType() + ";charset=utf-8");
            }

            if (httpHostRoute.redirectType()) {
                //todo return redirect tag
                fullHttpResponse = HttpResponseBuilder.redirectResponse(httpHostRoute.getForwardProtocol() + httpHostRoute.getRedirectAddress());
            }


            if (httpHostRoute.forwardType()) {
                //todo forward request

                //启动内部访问客户端


                LiteHttpProxy liteHttpProxy = LiteHttpProxyPool.getLiteHttpProxy();
                fullHttpResponse = liteHttpProxy.forward(httpHostRoute, fullHttpRequest.retain());
                liteHttpProxy.release();

            }

        } else {
            fullHttpResponse = HttpResponseBuilder.htmlResponse(ServerRuntimeConfig.ROUTE_NOT_FOUND_CONTENT.getBytes());
        }

        channelHandlerContext.writeAndFlush(fullHttpResponse);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
