package com.view.core.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.ResourceLeakDetector;

import java.util.List;

/**
 * 接编码器
 */
public class NDCPCodec extends ByteToMessageCodec<NDCPacket> {

    //NDCPCodec
    public static final String NAME = "NDC";

    private volatile NDCPacket ndcPacket;


    static {
        //设置Netty泄露检测的级别
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NDCPacket NdcPacket, ByteBuf byteBuf) {
        //拆包
        List<NDCPacket> list = NdcPacket.autoUnpack();

        list.forEach(x -> {
            byteBuf.writeBytes(x.toByteArray());
        });

    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        int i = byteBuf.readableBytes();
        if (ndcPacket == null) {
            //todo 未完成报文填充
            if (i >= NDCPacket.FIX_LENGTH) {
                //todo 固定长度获取完成
                byte[] bytes = new byte[NDCPacket.FIX_LENGTH];
                byteBuf.readBytes(bytes);
                try {
                    ndcPacket = NDCPacket.parseFixInfo(bytes);//解析定长信息
                } catch (Exception e) {
                    //todo 解析失败直接关闭连接
                    channelHandlerContext.channel().closeFuture();
                    return;
                }

                if (ndcPacket.getDataSize() == 0) {
                    list.add(ndcPacket);
                    ndcPacket = null;
                    //丢弃已读字节
                    byteBuf.discardReadBytes();
                }
            } else {
                //todo 固定长度未获取完成
            }
        } else {
            //todo 获取到固定长度
            int dataSize = ndcPacket.getDataSize();
            if (i >= dataSize) {
                //todo 获取到足够动态长度
                byte[] bytes = new byte[dataSize];
                byteBuf.readBytes(bytes);
                ndcPacket.setDataWithVerification(bytes);
                list.add(ndcPacket);
                ndcPacket = null;
                //丢弃已读字节
                byteBuf.discardReadBytes();
            } else {
                //todo 未获取到足够动态长度
            }
        }
    }
}

