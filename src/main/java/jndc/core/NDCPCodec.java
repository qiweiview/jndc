package jndc.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakDetector;

import java.util.List;

public class NDCPCodec extends ByteToMessageCodec<NDCMessageProtocol> {

    public static final String NAME="NDC";
    private NDCMessageProtocol ndcMessageProtocol;


    static {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol NDCMessageProtocol, ByteBuf byteBuf) throws Exception {
        //auto unpack
        List<NDCMessageProtocol> list = NDCMessageProtocol.autoUnpack();
        list.forEach(x->{
            byteBuf.writeBytes(x.toByteArray());
        });

    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        int i = byteBuf.readableBytes();
        if (i >= NDCMessageProtocol.FIX_LENGTH ) {
            if ( ndcMessageProtocol == null){
                byte[] bytes = new byte[NDCMessageProtocol.FIX_LENGTH];
                byteBuf.readBytes(bytes);
                ndcMessageProtocol = NDCMessageProtocol.parseFixInfo(bytes);//解析定长信息
            }else {
                int dataSize = ndcMessageProtocol.getDataSize();//获取变长报文长度
                if (i >= dataSize) {
                    byte[] bytes = new byte[dataSize];
                    byteBuf.readBytes(bytes);
                    ndcMessageProtocol.setDataWithVerification(bytes);
                    list.add(ndcMessageProtocol);
                    ndcMessageProtocol=null;
                    byteBuf.discardReadBytes();//丢弃已读字节
                } else {
                    //todo not enough bytes
                }
            }

        }



    }
}
