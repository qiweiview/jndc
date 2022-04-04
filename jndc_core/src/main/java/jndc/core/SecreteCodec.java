package jndc.core;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import jndc.utils.AESDataEncryption;
import jndc.utils.DataEncryption;

import java.util.List;


/**
 * 加解密
 */
public class SecreteCodec extends MessageToMessageCodec<NDCMessageProtocol, NDCMessageProtocol> {
    public static final String NAME = "SECRETE_CODEC";

    private static DataEncryption dataEncryption = new AESDataEncryption();//Can be changed to a more secure encryption algorithm

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol, List<Object> list) throws Exception {
        byte[] data = ndcMessageProtocol.getData();
        ndcMessageProtocol.setData(dataEncryption.encode(data));
        list.add(ndcMessageProtocol);
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol, List<Object> list) throws Exception {
        byte[] data = ndcMessageProtocol.getData();
        ndcMessageProtocol.setData(dataEncryption.decode(data));
        list.add(ndcMessageProtocol);
    }
}
