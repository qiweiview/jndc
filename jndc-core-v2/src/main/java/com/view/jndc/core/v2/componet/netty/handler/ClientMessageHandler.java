package com.view.jndc.core.v2.componet.netty.handler;

import com.view.jndc.core.v2.enum_value.JNDCMessageType;
import com.view.jndc.core.v2.model.jndc.JNDCData;
import com.view.jndc.core.v2.model.json_object.ChannelRegister;
import com.view.jndc.core.v2.model.json_object.DataTransmission;
import com.view.jndc.core.v2.model.json_object.JSONSerializable;
import com.view.jndc.core.v2.model.json_object.ServiceRegister;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * 单个客户端实例
 */
@Slf4j
public class ClientMessageHandler extends WriteableHandler<JNDCData> {
    public static final String NAME = "ClientMessageHandler";

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
        byte[] data = jndcData.getData();

        if (JNDCMessageType.CHANNEL_0X10.value == type) {
            ChannelRegister channelRegister = JSONSerializable.deserialize(data, ChannelRegister.class);
            if (channelRegister.successCheck()) {
                log.info(channelRegister.getMsg());
            } else {
                log.error(channelRegister.getMsg());
            }

        } else if (JNDCMessageType.CHANNEL_SERVICE_0X11.value == type) {
            //todo 【服务端】处理服务注册消息
            ServiceRegister serviceRegister = JSONSerializable.deserialize(data, ServiceRegister.class);
            if (serviceRegister.successCheck()) {
                log.info(serviceRegister.getMsg());
            } else {
                log.error(serviceRegister.getMsg());
            }

        } else if (JNDCMessageType.CHANNEL_SERVICE_0X12.value == type) {
            //todo 连接打开
            DataTransmission dataTransmission = JSONSerializable.deserialize(data, DataTransmission.class);

        } else if (JNDCMessageType.CHANNEL_SERVICE_0X13.value == type) {
            //todo 数据传输包
            DataTransmission dataTransmission = JSONSerializable.deserialize(data, DataTransmission.class);

        } else if (JNDCMessageType.CHANNEL_SERVICE_0X14.value == type) {
            //todo 连接中断
            DataTransmission dataTransmission = JSONSerializable.deserialize(data, DataTransmission.class);

        } else if (JNDCMessageType.TEST_BANDWIDTH_0X20.value == type) {


        } else if (JNDCMessageType.HAPPY_EVERY_DAY.value == type) {


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
