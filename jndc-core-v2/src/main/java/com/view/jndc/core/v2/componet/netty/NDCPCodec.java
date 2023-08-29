package com.view.jndc.core.v2.componet.netty;

import com.view.jndc.core.v2.constant.protocol_message.StaticConfig;
import com.view.jndc.core.v2.model.protocol_message.JNDCEncoded;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.ResourceLeakDetector;

import java.util.List;

/**
 * 接编码器
 */
public class NDCPCodec extends ByteToMessageCodec<JNDCEncoded> {

    public static final String NAME = "NDC";

    private volatile JNDCEncoded ndcMessageProtocol;


    static {
        //设置Netty泄露检测的级别
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, JNDCEncoded NDCMessageProtocol, ByteBuf byteBuf) {
        //auto unpack
        List<JNDCEncoded> list = NDCMessageProtocol.autoUnpack();
        list.forEach(x -> {
            byteBuf.writeBytes(x.toTransferFormat());
        });

    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        int i = byteBuf.readableBytes();
        if (ndcMessageProtocol == null) {
            //todo 未完成报文填充
            if (i >= StaticConfig.MESSAGE_VERIFICATION_LENGTH) {
                //todo 获取到定长字节
                byte[] bytes = new byte[StaticConfig.MESSAGE_VERIFICATION_LENGTH];

                //仅读取定长字节
                byteBuf.readBytes(bytes);
                ndcMessageProtocol = JNDCEncoded.toEncodedFormat(bytes);//解析定长信息
            } else {
                //todo 未获取到足够长度定长字节
            }
        } else {
            //todo 获取到固定长度
            int dataSize = ndcMessageProtocol.getDataSize();//获取变长报文长度
            if (i >= dataSize) {
                //todo 获取到足够动态长度
                byte[] bytes = new byte[dataSize];
                byteBuf.readBytes(bytes);
                ndcMessageProtocol.setDataWithVerification(bytes);
                list.add(ndcMessageProtocol);
                ndcMessageProtocol = null;

                //丢弃已读字节
                byteBuf.discardReadBytes();
            } else {
                //todo 未获取到足够动态长度
            }
        }
    }
}
