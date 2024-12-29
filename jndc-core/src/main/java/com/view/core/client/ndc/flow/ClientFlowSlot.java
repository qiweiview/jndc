package com.view.core.client.ndc.flow;

import com.view.core.client.ndc.NDCClientConfiguration;
import com.view.core.model.local_service.LocalService;
import com.view.core.protocol.NDCPacket;
import com.view.core.protocol.NDCPacketBuilder;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 客户端流程槽
 */
@Data
@Slf4j
public abstract class ClientFlowSlot {

    private Supplier<String> stingIdGetter;

    private Supplier<Long> longIdGetter;

    private Supplier<String> clientIdGetter;

    private Supplier<ChannelHandlerContext> channelHandlerContextGetter;

    private Supplier<NDCClientConfiguration> ndcClientConfigurationGetter;


    /**
     * @param localService
     * @param persistent   是否内存持久化，true则客户端重连时会自动注册
     */
    public void registerServiceManual(LocalService localService, boolean persistent) {

        NDCPacket registerServicePacket = NDCPacketBuilder.registerServicePacket(localService);
        if (persistent) {
            NDCClientConfiguration ndcClientConfiguration = ndcClientConfigurationGetter.get();
            ndcClientConfiguration.distinctAddRegisterService(registerServicePacket);
        }

        ChannelHandlerContext context = channelHandlerContextGetter.get();
        context.writeAndFlush(registerServicePacket);
    }

    /**
     * @param localService
     * @param persistent   是否移除内存，true则客户端重连时不再注册
     */
    public void unregisterServiceManual(LocalService localService, boolean persistent) {
        NDCPacket unregisterServicePacket = NDCPacketBuilder.unregisterServicePacket(localService);
        if (persistent) {
            NDCClientConfiguration ndcClientConfiguration = ndcClientConfigurationGetter.get();
            ndcClientConfiguration.removeRegisterService(unregisterServicePacket);
        }
        ChannelHandlerContext context = channelHandlerContextGetter.get();
        context.writeAndFlush(unregisterServicePacket);
    }


    public abstract void ndcClientStart();

    public abstract void ndcClientStartFail(Exception e);

    protected abstract void connectionActive();

    public abstract void openChannel();

    public abstract void registerTCPService(String serviceId);

    public abstract void unregisterTCPService(String serviceId);

    public abstract void tcpClientStart(String serviceId, String tcpChannelId);

    public abstract void tcpChannelActive(String serviceId, String tcpChannelId);

    public abstract void tcpClientStartFail(String serviceId, String tcpChannelId);

    public abstract void tcpChannelRead(String serviceId, String tcpChannelId, byte[] bytes);

    public abstract void tcpChannelInactive(String serviceId, String tcpChannelId);

    public abstract void tcpChannelWrite(String serviceId, String tcpChannelId, byte[] bytes);

    public abstract void ndcClientInActive();

    public abstract void ndcClientStop();

    //====safe call====

    public void ndcClientStartSafe() {
        try {
            ndcClientStart();
        } catch (Exception e) {
            log.error("ndcClientStartSafe call back error", e);
        }
    }

    public void ndcClientStartFailSafe(Exception e) {
        try {
            ndcClientStartFail(e);
        } catch (Exception e2) {
            log.error("ndcClientStartFailSafe call back error", e2);
        }
    }

    public void openChannelSafe() {
        try {
            openChannel();
        } catch (Exception e) {
            log.error("openChannelSafe call back error", e);
        }
    }

    public void registerTCPServiceSafe(String serviceId) {
        try {
            registerTCPService(serviceId);
        } catch (Exception e) {
            log.error("registerTCPServiceSafe call back error", e);
        }
    }

    public void unregisterTCPServiceSafe(String serviceId) {
        try {
            unregisterTCPService(serviceId);
        } catch (Exception e) {
            log.error("unregisterTCPServiceSafe call back error", e);
        }
    }

    public void tcpClientStartSafe(String serviceId, String tcpChannelId) {
        try {
            tcpClientStart(serviceId, tcpChannelId);
        } catch (Exception e) {
            log.error("tcpClientStartSafe call back error", e);
        }
    }

    public void tcpChannelActiveSafe(String serviceId, String tcpChannelId) {
        try {
            tcpChannelActive(serviceId, tcpChannelId);
        } catch (Exception e) {
            log.error("tcpChannelActiveSafe call back error", e);
        }
    }

    public void tcpClientStartFailSafe(String serviceId, String tcpChannelId) {
        try {
            tcpClientStartFail(serviceId, tcpChannelId);
        } catch (Exception e) {
            log.error("tcpClientStartFailSafe call back error", e);
        }
    }

    public void tcpChannelReadSafe(String serviceId, String tcpChannelId, byte[] bytes) {
        try {
            tcpChannelRead(serviceId, tcpChannelId, bytes);
        } catch (Exception e) {
            log.error("tcpChannelReadSafe call back error", e);
        }
    }

    public void tcpChannelInactiveSafe(String serviceId, String tcpChannelId) {
        try {
            tcpChannelInactive(serviceId, tcpChannelId);
        } catch (Exception e) {
            log.error("tcpChannelInactiveSafe call back error", e);
        }
    }

    public void tcpChannelWriteSafe(String serviceId, String tcpChannelId, byte[] bytes) {
        try {
            tcpChannelWrite(serviceId, tcpChannelId, bytes);
        } catch (Exception e) {
            log.error("tcpChannelWriteSafe call back error", e);
        }
    }

    public void ndcClientInActiveSafe() {
        try {
            ndcClientInActive();
        } catch (Exception e) {
            log.error("ndcClientInActiveSafe call back error", e);
        }

    }

    public void ndcClientStopSafe() {
        try {
            ndcClientStop();
        } catch (Exception e) {
            log.error("ndcClientStopSafe call back error", e);
        }
    }

    public void connectionActiveSafe() {
        try {
            connectionActive();
        } catch (Exception e) {
            log.error("connectionActiveSafe call back error", e);
        }
    }

}

