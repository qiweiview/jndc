package jndc.core;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import jndc.exception.SecreteDecodeFailException;
import jndc.utils.AESDataEncryption;
import jndc.utils.DataEncryption;
import jndc.utils.InetUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * 加解密
 */
@Slf4j
public class SecreteCodec extends MessageToMessageCodec<NDCMessageProtocol, NDCMessageProtocol> {
    public static final String NAME = "SECRETE_CODEC";

    //对称加密
    private static DataEncryption dataEncryption = new AESDataEncryption();

    /**
     * 加密
     *
     * @param channelHandlerContext
     * @param ndcMessageProtocol
     * @param list
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol, List<Object> list) throws Exception {
        //仅针对数据部分做处理
        byte[] data = ndcMessageProtocol.getData();
        ndcMessageProtocol.setData(dataEncryption.encode(data));
        list.add(ndcMessageProtocol);
    }

    /**
     * 解密
     *
     * @param channelHandlerContext
     * @param ndcMessageProtocol
     * @param list
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, NDCMessageProtocol ndcMessageProtocol, List<Object> list) throws Exception {
        if (ndcMessageProtocol.NO_ACCESS == ndcMessageProtocol.getType()) {
            log.error("连接密码错误...");
            System.exit(1);
        }

        //仅针对数据部分做处理
        byte[] data = ndcMessageProtocol.getData();
        try {
            ndcMessageProtocol.setData(dataEncryption.decode(data));
            list.add(ndcMessageProtocol);
        } catch (SecreteDecodeFailException exception) {
            //todo 解码异常

            //发送异常提示消息
            NDCMessageProtocol err = NDCMessageProtocol.of(InetUtils.localInetAddress, InetUtils.localInetAddress, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.UN_USED_PORT, NDCMessageProtocol.NO_ACCESS);
            ndcMessageProtocol.setData(NDCMessageProtocol.BLANK);
            channelHandlerContext.writeAndFlush(err);
            channelHandlerContext.close();//关闭链接
        }

    }
}
