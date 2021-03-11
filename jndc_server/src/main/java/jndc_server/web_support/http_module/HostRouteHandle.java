package jndc_server.web_support.http_module;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import jndc.core.UniqueBeanManage;
import jndc_server.web_support.model.data_object.HttpHostRoute;
import jndc_server.web_support.utils.BlockValueFeature;
import jndc_server.web_support.utils.HttpResponseBuilder;

public class HostRouteHandle extends SimpleChannelInboundHandler<FullHttpRequest> {
    public static String NAME = "HOST_ROUTE_HANDLE";


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws Exception {

        HttpHeaders headers = fullHttpRequest.headers();

        HostRouterComponent hostRouterComponent = UniqueBeanManage.getBean(HostRouterComponent.class);
//        String host = headers.get(HttpHeaderNames.HOST);
//        HttpHostRoute httpHostRoute = hostRouterComponent.matchHost(host);
//        FullHttpResponse fullHttpResponse = null;
//        if (httpHostRoute != null) {
//            if (httpHostRoute.fromFixValue()) {
//                //todo return fix value
//                fullHttpResponse = HttpResponseBuilder.defaultResponse(httpHostRoute.getFixedResponse().getBytes(), httpHostRoute.getFixedContentType() + ";charset=utf-8");
//            }
//
//            if (httpHostRoute.fromRedirect()) {
//                //todo return redirect tag
//                fullHttpResponse = HttpResponseBuilder.redirectResponse(httpHostRoute.getRedirectAddress());
//            }
//
//
//            if (httpHostRoute.fromForward()) {
//                //todo forward request
//                BlockValueFeature<FullHttpResponse> forward = new LiteHttpProxy(channelHandlerContext, httpHostRoute, fullHttpRequest).forward();
//
//                //wait for 15 second
//                fullHttpResponse = forward.get(15);
//            }
//
//        } else {
//            fullHttpResponse = HttpResponseBuilder.textResponse("不存该路径匹配规则".getBytes());
//        }
        FullHttpResponse fullHttpResponse = null;
        BlockValueFeature<FullHttpResponse> forward = new LiteHttpProxy( null,fullHttpRequest.retain()).forward();

        //wait for 15 second
        fullHttpResponse = forward.get(15);
        channelHandlerContext.writeAndFlush(fullHttpResponse.copy());


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
