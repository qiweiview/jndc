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
            String info = "" +
                    "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "\t<head>\n" +
                    "\t\t<meta charset=\"utf-8\">\n" +
                    "\t\t<title></title>\n" +
                    "\t</head>\n" +
                    "\t<body>\n" +
                    "\t\t<div style=\"text-align: center;font-size: 85px;margin-top: 20vh;\">\uD83D\uDEEB\uD83D\uDEEB\uD83D\uDEEBNot Found</div>\n" +
                    "\t\t<div style=\"text-align: center;position: fixed;bottom: 45px;width:100vw;\">\n" +
                    "\t\t\t<span style=\"margin-left: 15px;color: black;font-size: 18px;margin-right:15px;\">Copyright © 2020 View.\n" +
                    "\t\t\t\t保留所有权利 |</span>\n" +
                    "\t\t\t<a href=\"http://beian.miit.gov.cn\" rel=\"noreferrer\" target=\"_blank\"\n" +
                    "\t\t\t\tstyle=\"color: black;font-size: 25px;\">\n" +
                    "\t\t\t\t闽ICP备17002953号\n" +
                    "\t\t\t</a>\n" +
                    "\t\t</div>\n" +
                    "\t</body>\n" +
                    "</html>\n" +
                    "";
            fullHttpResponse = HttpResponseBuilder.htmlResponse(info.getBytes());
        }

        channelHandlerContext.writeAndFlush(fullHttpResponse);


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
