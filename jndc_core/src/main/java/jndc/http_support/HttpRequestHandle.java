package jndc.http_support;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import jndc.http_support.model.NettyRequest;
import jndc.http_support.model.ResponseDescription;
import jndc.utils.PackageScan;
import lombok.Data;

import java.util.List;


@Data
public class HttpRequestHandle extends SimpleChannelInboundHandler<FullHttpRequest> {
    public static String NAME = "HOST_ROUTE_HANDLE";

    private MappingRegisterCenter mappingRegisterCenter;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) {
        NettyRequest nettyRequest = NettyRequest.of(fullHttpRequest);
        String s = "jndc_client.http_support";
        List<Class> classes = PackageScan.scanClass(s);

        if (mappingRegisterCenter == null) {
            throw new RuntimeException("can not found the mappingRegisterCenter");
        }
        ResponseDescription responseDescription = mappingRegisterCenter.invokeMapping(nettyRequest);

        FullHttpResponse fullHttpResponse;
        if (responseDescription == null || responseDescription.getData() == null) {
            fullHttpResponse = HttpResponseBuilder.notFoundResponse();
        } else {
            WebMapping.RESPONSE_TYPE responseType = responseDescription.getResponseType();
            if (responseType == WebMapping.RESPONSE_TYPE.HTML) {
                fullHttpResponse = HttpResponseBuilder.htmlResponse(responseDescription.getData());
            } else if (responseType == WebMapping.RESPONSE_TYPE.JSON) {
                fullHttpResponse = HttpResponseBuilder.jsonResponse(responseDescription.getData());
            } else {
                fullHttpResponse = HttpResponseBuilder.textResponse(responseDescription.getData());
            }
        }
        channelHandlerContext.writeAndFlush(fullHttpResponse);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }
}
