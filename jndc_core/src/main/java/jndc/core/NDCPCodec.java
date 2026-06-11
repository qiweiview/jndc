package jndc.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.ResourceLeakDetector;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 接编码器
 */
@Slf4j
public class NDCPCodec extends ByteToMessageCodec<NDCMessageProtocol> {

    public static final String NAME="NDC";

    private volatile NDCMessageProtocol ndcMessageProtocol;


    static {
        //设置Netty泄露检测的级别
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.ADVANCED);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol NDCMessageProtocol, ByteBuf byteBuf) {
        //auto unpack
        List<NDCMessageProtocol> list = NDCMessageProtocol.autoUnpack();
        list.forEach(x -> {
            byteBuf.writeBytes(x.toByteArray());
        });

    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) {
        int i = byteBuf.readableBytes();
        if (ndcMessageProtocol == null) {
            //todo 未完成报文填充
            if (i >= NDCMessageProtocol.FIX_LENGTH) {
                //todo 固定长度获取完成
                byte[] bytes = new byte[NDCMessageProtocol.FIX_LENGTH];
                byteBuf.readBytes(bytes);
                try {
                    ndcMessageProtocol = NDCMessageProtocol.parseFixInfo(bytes);//解析定长信息
                } catch (RuntimeException e) {
                    log.error("协议头解析失败: " + e.getMessage());
                    channelHandlerContext.close();
                    return;
                }
                if (ndcMessageProtocol.getDataSize() == 0) {
                    list.add(ndcMessageProtocol);
                    ndcMessageProtocol=null;
                }
            }else {
                //todo 固定长度未获取完成
            }
        }else {
            //todo 获取到固定长度
            int dataSize = ndcMessageProtocol.getDataSize();//获取变长报文长度

            // 防御：dataSize 为负数或超过最大单包长度时，视为协议异常，关闭连接
            if (dataSize < 0 || dataSize > NDCMessageProtocol.AUTO_UNPACK_LENGTH) {
                log.error("协议异常：dataSize 超限 dataSize=" + dataSize);
                ndcMessageProtocol = null;
                channelHandlerContext.close();
                return;
            }

            if (i >= dataSize) {
                //todo 获取到足够动态长度
                byte[] bytes = new byte[dataSize];
                byteBuf.readBytes(bytes);
                ndcMessageProtocol.setDataWithVerification(bytes);
                list.add(ndcMessageProtocol);
                ndcMessageProtocol = null;
            }else {
                //todo 未获取到足够动态长度
            }
        }
    }
}
