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
                //overwrite the host
                fullHttpRequest.headers().set(HttpHeaderNames.HOST, httpHostRoute.getForwardHost() + ":" + httpHostRoute.getForwardPort());
                BlockValueFeature<FullHttpResponse> forward = new LiteHttpProxy(httpHostRoute, fullHttpRequest.retain()).forward();

                //wait for 15 second
                fullHttpResponse = forward.get(15);
            }

        } else {
            String info = " <div style=\"text-align: center;position: fixed;bottom: 45px;width:100vw;\">\n" +
                    "      <span\n" +
                    "        style=\"margin-left: 15px;color: white;font-size: 18px;margin-right:15px;\">Copyright © 2020 View. 保留所有权利 |</span>\n" +
                    "      <a href=\"http://www.beian.miit.gov.cn\" rel=\"noreferrer\" target=\"_blank\" style=\"color: white;font-size: 25px;\">\n" +
                    "        闽ICP备17002953号\n" +
                    "      </a>\n" +
                    "    </div>";
            fullHttpResponse = HttpResponseBuilder.htmlResponse(("<div/>不存该路径匹配规</div>" + info).getBytes());
        }

        channelHandlerContext.writeAndFlush(fullHttpResponse);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
