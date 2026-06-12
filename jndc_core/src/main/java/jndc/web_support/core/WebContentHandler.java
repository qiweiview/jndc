package jndc.web_support.core;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import jndc.core.UniqueBeanManage;
import jndc.web_support.utils.HttpResponseBuilder;
import lombok.extern.slf4j.Slf4j;


/**
 * 管理端api处理器
 */
@Slf4j
public class WebContentHandler extends SimpleChannelInboundHandler<JNDCHttpRequest> {
    public static String NAME = "WEB_CONTENT_HANDLER";

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, JNDCHttpRequest jndcHttpRequest) throws Exception {

        //get method
        if (HttpMethod.GET.equals(jndcHttpRequest.getMethod())) {
            FullHttpResponse fullHttpResponse = HttpResponseBuilder.notFoundResponse();
            channelHandlerContext.writeAndFlush(fullHttpResponse);
            return;
        }


        //post method
        if (HttpMethod.POST.equals(jndcHttpRequest.getMethod())) {
            //todo post
            MappingRegisterCenter mappingRegisterCenter = UniqueBeanManage.getBean(MappingRegisterCenter.class);
            byte[] data = mappingRegisterCenter.invokeMapping(jndcHttpRequest);
            FullHttpResponse fullHttpResponse;
            if (data == null) {
                fullHttpResponse = HttpResponseBuilder.notFoundResponse();
            } else {
                //todo 没有对应mapping
                fullHttpResponse = HttpResponseBuilder.jsonResponse(data);
            }
            fullHttpResponse.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS,"Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With,auth-token");
            channelHandlerContext.writeAndFlush(fullHttpResponse);
            return;
        }


        //不支持请求类型
        FullHttpResponse fullHttpResponse = HttpResponseBuilder.emptyResponse();
        fullHttpResponse.setStatus(HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE);
        channelHandlerContext.writeAndFlush(fullHttpResponse);

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("unCatchableException: " + cause);

    }
}
