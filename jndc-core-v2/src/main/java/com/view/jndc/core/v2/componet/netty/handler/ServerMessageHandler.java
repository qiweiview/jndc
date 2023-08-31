package com.view.jndc.core.v2.componet.netty.handler;

import com.view.jndc.core.v2.enum_value.JNDCMessageType;
import com.view.jndc.core.v2.model.jndc.JNDCData;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

@Slf4j
public class ServerMessageHandler extends WriteableHandler<JNDCData> {
    public static final String NAME = "ServerMessageHandler";

    private volatile ChannelHandlerContext context;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        context = ctx;
        synchronized (this) {
            notifyAll();
        }
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, JNDCData jndcData) throws Exception {
        byte type = jndcData.getType();
        SocketAddress socketAddress = channelHandlerContext.channel().remoteAddress();

        if (JNDCMessageType.CHANNEL_0X10.value == type) {
            log.info(socketAddress + "申请打开通道");
        } else if (JNDCMessageType.TEST_BANDWIDTH_0X20.value == type) {
            log.info(socketAddress + "在进行带宽测速，接收到128Mb");

        } else {
            log.error("无法识别的消息类型");
        }

    }

    @Override
    public void write(JNDCData jndcData) {
        if (context == null) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    log.error("等待异常", e);
                }
            }
        }
        context.writeAndFlush(jndcData);
    }
}
