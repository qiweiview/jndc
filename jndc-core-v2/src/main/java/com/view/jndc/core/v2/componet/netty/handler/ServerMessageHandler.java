package com.view.jndc.core.v2.componet.netty.handler;

import com.view.jndc.core.v2.enum_value.JNDCMessageType;
import com.view.jndc.core.v2.model.jndc.JNDCData;
import com.view.jndc.core.v2.model.json_object.ChannelRegister;
import com.view.jndc.core.v2.model.json_object.DataTransmission;
import com.view.jndc.core.v2.model.json_object.JSONSerializable;
import com.view.jndc.core.v2.model.json_object.ServiceRegister;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通道单元
 */
@Slf4j
public class ServerMessageHandler extends WriteableHandler<JNDCData> {
    public static final String NAME = "ServerMessageHandler";

    private Map<String, ChannelRegister> map = new ConcurrentHashMap<>();

    private volatile ChannelHandlerContext context;

    //服务处理回调
    private final ServiceRequestHandleCallback callback = new ServiceRequestHandleCallback() {
        @Override
        public void active(String proxyId, String sourceId) {
            JNDCData dataTransmissionType = JNDCData.createConnectionActiveType();
            DataTransmission dataTransmission = new DataTransmission();
            dataTransmission.setProxyId(proxyId);
            dataTransmission.setSourceId(sourceId);
            write(dataTransmissionType);
        }

        @Override
        public void accept(String proxyId, String sourceId, byte[] data) {
            JNDCData dataTransmissionType = JNDCData.createDataTransmissionType();
            DataTransmission dataTransmission = new DataTransmission();
            dataTransmission.setProxyId(proxyId);
            dataTransmission.setSourceId(sourceId);
            dataTransmission.setData(data);
            dataTransmissionType.setData(dataTransmission.serialize());
            write(dataTransmissionType);
        }

        @Override
        public void inActive(String proxyId, String sourceId) {
            JNDCData dataTransmissionType = JNDCData.createConnectionInActiveType();
            DataTransmission dataTransmission = new DataTransmission();
            dataTransmission.setProxyId(proxyId);
            dataTransmission.setSourceId(sourceId);
            write(dataTransmissionType);
        }
    };

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
        byte[] data = jndcData.getData();

        if (JNDCMessageType.CHANNEL_0X10.value == type) {
            //todo 【服务端】通道开通
            ChannelRegister channelRegister = JSONSerializable.deserialize(data, ChannelRegister.class);
            String channelId = channelRegister.getChannelId();
            ChannelRegister exist = map.get(channelId);
            if (exist == null) {
                //todo 通道初次打开,执行初始化
                log.info("通道开通成功");
                channelRegister.success("通道开通成功");
            } else {
                //todo 存在同名编号
                log.info("通道开通成功，覆盖同名通道");
                channelRegister.success("通道开通，覆盖同名通道");
                exist.shutdown();

            }
            map.put(channelId, channelRegister);


            //响应
            jndcData.setData(channelRegister.serialize());
            write(jndcData);


        } else if (JNDCMessageType.CHANNEL_SERVICE_0X11.value == type) {
            //todo 【服务端】服务注册
            ServiceRegister serviceRegister = JSONSerializable.deserialize(data, ServiceRegister.class);
            //设置回调
            serviceRegister.setDataHandler(callback);

            String sourceChannelId = serviceRegister.getSourceChannelId();
            ChannelRegister channel = map.get(sourceChannelId);
            if (channel == null) {
                //todo 异常情况，通道不存在
                log.error("不存在编号为" + sourceChannelId + "的通道");
                serviceRegister.fail("不存在编号为" + sourceChannelId + "的通道");
            } else {
                //todo 存在同名编号
                channel.bindServiceOnServer(serviceRegister.getServiceId(), serviceRegister);
                log.info("注册服务【" + serviceRegister.getServiceName() + "】成功");
                serviceRegister.success("注册服务【" + serviceRegister.getServiceName() + "】成功");
            }


            //响应
            jndcData.setData(serviceRegister.serialize());
            write(jndcData);

        } else if (JNDCMessageType.CHANNEL_SERVICE_0X13.value == type) {
            //todo 数据传输包

        } else if (JNDCMessageType.TEST_BANDWIDTH_0X20.value == type) {
            int dataSize = jndcData.getDataSize();
            log.info(socketAddress + "在进行带宽测速，接收到" + (dataSize / 1024 / 1024) + "mb");

        } else if (JNDCMessageType.HAPPY_EVERY_DAY.value == type) {
            log.info(socketAddress + "收到问候");

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
