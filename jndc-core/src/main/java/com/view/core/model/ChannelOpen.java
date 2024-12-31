package com.view.core.model;

import com.view.core.model.heart_beat.HeartBeatPack;
import com.view.core.model.heart_beat.HeartBeatSource;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import com.view.core.server.tcp.TCPServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 通道打开
 */
@Data
@Slf4j
public class ChannelOpen implements Serializable {
    public static final long serialVersionUID = -4599902495744735536L;

    private String ndcClientId;

    private  Map<String, TCPServer> tcpServerMap = new ConcurrentHashMap<>();

    private transient ScheduledFuture<?> scheduledFuture;

    public void startHeartBeat(HeartBeatSource heartBeatSource, ChannelHandlerContext ndcContex) {
        log.info("开始心跳");
        //间隔3秒发送一次心跳
        scheduledFuture = ndcContex.executor().scheduleAtFixedRate(() -> {

            HeartBeatPack heartBeatPack = new HeartBeatPack();
            heartBeatPack.setNdcClientId(ndcClientId);
            heartBeatPack.setSource(heartBeatSource);
            NDCPacket ndcPacket = NDCPacketBuilder.heartBeatPacket(heartBeatPack);
            ndcContex.writeAndFlush(ndcPacket);
        }, 0, 3, TimeUnit.SECONDS);
    }

    public void stopHeartBeat() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
    }
}
