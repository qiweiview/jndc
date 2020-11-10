package web.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;

public class JNDCRequestDecoder extends MessageToMessageDecoder<FullHttpRequest> {
    public static String NAME="JNDC_REQUEST_DECODER";



    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest, List<Object> list) throws Exception {
        JNDCHttpRequest ndcHttpRequest = new JNDCHttpRequest(fullHttpRequest);
        InetSocketAddress socketAddress = (InetSocketAddress) channelHandlerContext.channel().remoteAddress();
        ndcHttpRequest.setRemoteAddress(socketAddress.getAddress());


        list.add(ndcHttpRequest);

    }




}
