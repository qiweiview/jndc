package jndc.web_support.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * jndc内http请求解码器
 */
public class JNDCRequestDecoder extends MessageToMessageDecoder<FullHttpRequest> {
    public static String NAME = "JNDC_REQUEST_DECODER";


    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest, List<Object> list) throws Exception {

        if (fullHttpRequest.headers().get(HttpHeaderNames.UPGRADE) != null) {
            //todo not ignore header and upgrade is not null
            channelHandlerContext.fireChannelRead(fullHttpRequest.retain());
        } else {
            //todo do http parse only
            JNDCHttpRequest ndcHttpRequest = new JNDCHttpRequest(fullHttpRequest);
            InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
            ndcHttpRequest.setRemoteAddress(socketAddress.getAddress());
            list.add(ndcHttpRequest);
        }
    }


}
