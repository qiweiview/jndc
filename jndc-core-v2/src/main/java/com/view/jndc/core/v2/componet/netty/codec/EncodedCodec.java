package com.view.jndc.core.v2.componet.netty.codec;

import com.view.jndc.core.v2.constant.protocol_message.StaticConfig;
import com.view.jndc.core.v2.exception.UnSupportedProtocolException;
import com.view.jndc.core.v2.model.protocol_message.JNDCEncoded;
import com.view.jndc.core.v2.utils.ByteConversionUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 接编码器
 */
@Slf4j
public class EncodedCodec extends ByteToMessageCodec<JNDCEncoded> {

    public static final String NAME = "NDCPCodec";

    private volatile JNDCEncoded ndcMessageProtocol;


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, JNDCEncoded jndcEncoded, ByteBuf byteBuf) {
        //auto unpack
        List<JNDCEncoded> list = jndcEncoded.autoUnpack();
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
                try {
                    ndcMessageProtocol = JNDCEncoded.toEncodedFormat(bytes);//解析定长信息
                } catch (UnSupportedProtocolException exception) {
                    ByteBuf notice = Unpooled.copiedBuffer("protocol mismatch", StandardCharsets.UTF_8);
                    channelHandlerContext.writeAndFlush(notice);
                    channelHandlerContext.close();
                }
            } else {
                //todo 未获取到足够长度定长字节
            }
        } else {
            //todo 获取到固定长度
            int dataSize = ByteConversionUtil.byteArrayToInt(ndcMessageProtocol.getDataSize());//获取变长报文长度
            if (i >= dataSize) {
                //todo 获取到足够动态长度
                byte[] bytes = new byte[dataSize];
                byteBuf.readBytes(bytes);
                ndcMessageProtocol.setData(bytes);
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
