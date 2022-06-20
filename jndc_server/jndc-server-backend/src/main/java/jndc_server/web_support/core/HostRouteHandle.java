package jndc_server.web_support.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import jndc.core.UniqueBeanManage;
import jndc.web_support.core.JNDCHttpRequest;
import jndc.web_support.http_module.HostRouterComponent;
import jndc.web_support.http_module.LiteHttpProxy;
import jndc.web_support.http_module.LiteHttpProxyPool;
import jndc.web_support.model.d_o.HttpHostRoute;
import jndc.web_support.utils.HttpResponseBuilder;
import jndc_server.config.ServerRuntimeConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * 服务器路由
 */
@Slf4j
public class HostRouteHandle extends SimpleChannelInboundHandler<FullHttpRequest> {
    public static String NAME = "HOST_ROUTE_HANDLE";


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {

        HttpHeaders headers = fullHttpRequest.headers();

        HostRouterComponent hostRouterComponent = UniqueBeanManage.getBean(HostRouterComponent.class);
        //提取host头
        String host = headers.get(HttpHeaderNames.HOST);
        // host="blog.ab.com";
        HttpHostRoute httpHostRoute = hostRouterComponent.matchHost(host);
        FullHttpResponse fullHttpResponse = null;
        if (httpHostRoute != null) {


            if (httpHostRoute.fixValueType()) {
                //todo 固定值
                fullHttpResponse = HttpResponseBuilder.defaultResponse(httpHostRoute.getFixedResponse().getBytes(), httpHostRoute.getFixedContentType() + ";charset=utf-8");
            }

            if (httpHostRoute.redirectType()) {
                //todo 重定向
                fullHttpResponse = HttpResponseBuilder.redirectResponse(httpHostRoute.getForwardProtocol() + httpHostRoute.getRedirectAddress());
            }


            if (httpHostRoute.forwardType()) {
                //todo 转发

                //从连接器池中获取访问客户端
                LiteHttpProxy liteHttpProxy = LiteHttpProxyPool.getLiteHttpProxy();
                new JNDCHttpRequest(fullHttpRequest.copy());
                fullHttpResponse = liteHttpProxy.forward(httpHostRoute, fullHttpRequest.retain());
                if (fullHttpResponse == null) {
                    //todo 超时的直接断开
                    channelHandlerContext.close();
                    return;
                }
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
