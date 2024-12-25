package com.view.core.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 接编码器
 */
@Slf4j
public class NDCPCodec extends ByteToMessageCodec<NDCPacket> {

    private volatile NDCPacket ndcPacket;

    private final ReentrantLock lock = new ReentrantLock();

    static {
        // 在开发环境时启用高级泄露检测，生产环境可以调整为低级别或禁用
        if (isDevelopmentEnvironment()) {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
        } else {
            ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NDCPacket ndcPacket, ByteBuf byteBuf) {
        lock.lock();
        try {
            List<NDCPacket> list = ndcPacket.autoUnpack();
            for (NDCPacket packet : list) {
                byteBuf.writeBytes(packet.toByteArray());
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        int readableBytes = byteBuf.readableBytes();

        if (ndcPacket == null) {
            // 处理未完成的报文
            if (readableBytes >= NDCPacket.FIX_LENGTH) {
                byte[] bytes = new byte[NDCPacket.FIX_LENGTH];
                byteBuf.readBytes(bytes);
                try {
                    ndcPacket = NDCPacket.parseFixInfo(bytes); // 解析定长信息
                } catch (Exception e) {
                    //todo 解析失败，关闭连接
                    channelHandlerContext.channel().close();
                    return;
                }

                if (ndcPacket.getDataSize() == 0) {
                    list.add(ndcPacket);
                    ndcPacket = null;
                    byteBuf.discardReadBytes(); // 丢弃已读字节
                }
            } else {
                // todo 等待更多的字节直到解析出定长信息

            }
        } else {
            // todo 处理动态长度数据
            int dataSize = ndcPacket.getDataSize();
            if (readableBytes >= dataSize) {
                byte[] bytes = new byte[dataSize];
                byteBuf.readBytes(bytes);
                ndcPacket.setDataWithVerification(bytes);
                list.add(ndcPacket);
                ndcPacket = null;
                byteBuf.discardReadBytes(); // 丢弃已读字节
            } else {
                // todo 等待更多的字节直到解析出动态长度数据
            }
        }
    }

    // 判断是否是开发环境（通过环境变量、系统属性等方式进行判断）
    private static boolean isDevelopmentEnvironment() {
        String env = System.getenv("ENVIRONMENT");
        return "development".equalsIgnoreCase(env);
    }
}
