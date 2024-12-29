package com.view.core.server.ndc.flow;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * 服务流程槽
 */
@Data
@Slf4j
public abstract class ServerFlowSlot {
    private String serverId;


    public abstract void ndcServerStart();

    public abstract void ndcServerStartFail(Exception e);

    public abstract void connectActive();

    public abstract void openChannel(String clientId);

    public abstract void tcpServerStartSuccess(String ndcClientId, String serviceId);

    public abstract void tcpServerStartFail(String ndcClientId, String serviceId);

    public abstract void tcpChannelActive(String ndcClientId, String serviceId, String tcpChannelId);

    public abstract void tcpChannelRead(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress remote, byte[] data);

    public abstract void tcpChannelWrite(String ndcClientId, String serviceId, String tcpChannelId, byte[] data);

    public abstract void tcpChannelInactive(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress remote);

    public abstract void tcpServerStop(String ndcClientId, String serviceId);

    public abstract void connectInActive(String clientId);

    public abstract void ndcServerStop();


    //====safe call====
    public final void ndcServerStartSafe() {
        try {
            ndcServerStart();
        } catch (Exception e) {
            log.error("ndcServerStartSafe call back error", e);
        }
    }

    public final void ndcServerStartFailSafe(Exception e) {
        try {
            ndcServerStartFail(e);
        } catch (Exception e2) {
            log.error("ndcServerStartFailSafe call back error", e2);
        }
    }

    public final void connectActiveSafe() {
        try {
            connectActive();
        } catch (Exception e) {
            log.error("connectActiveSafe call back error", e);
        }
    }

    public final void openChannelSafe(String clientId) {
        try {
            openChannel(clientId);
        } catch (Exception e) {
            log.error("openChannelSafe call back error", e);
        }
    }

    public final void tcpServerStartSuccessSafe(String ndcClientId, String serviceId) {
        try {
            tcpServerStartSuccess(ndcClientId, serviceId);
        } catch (Exception e) {
            log.error("tcpServerStartSuccessSafe call back error", e);
        }
    }

    public final void tcpServerStartFailSafe(String ndcClientId, String serviceId) {
        try {
            tcpServerStartFail(ndcClientId, serviceId);
        } catch (Exception e) {
            log.error("tcpServerStartFailSafe call back error", e);
        }
    }

    public final void tcpChannelActiveSafe(String ndcClientId, String serviceId, String tcpChannelId) {
        try {
            tcpChannelActive(ndcClientId, serviceId, tcpChannelId);
        } catch (Exception e) {
            log.error("tcpChannelActiveSafe call back error", e);
        }
    }

    public final void tcpChannelReadSafe(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress remote, byte[] data) {
        try {
            tcpChannelRead(ndcClientId, serviceId, tcpChannelId, remote, data);
        } catch (Exception e) {
            log.error("tcpChannelReadSafe call back error", e);
        }
    }

    public final void tcpChannelInactiveSafe(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress remote) {
        try {
            tcpChannelInactive(ndcClientId, serviceId, tcpChannelId, remote);
        } catch (Exception e) {
            log.error("tcpChannelInactiveSafe call back error", e);
        }
    }

    public final void tcpServerStopSafe(String ndcClientId, String serviceId) {
        try {
            tcpServerStop(ndcClientId, serviceId);
        } catch (Exception e) {
            log.error("tcpServerStop call back error", e);
        }
    }

    public final void connectInActiveSafe(String clientId) {
        try {
            connectInActive(clientId);
        } catch (Exception e) {
            log.error("connectInActive call back error", e);
        }
    }

    public final void ndcServerStopSafe() {
        try {
            ndcServerStop();
        } catch (Exception e) {
            log.error("ndcServerStop call back error", e);
        }
    }

    public void tcpChannelWriteSafe(String ndcClientId, String serviceId, String tcpChannelId, byte[] data) {
        try {
            tcpChannelWrite(ndcClientId, serviceId, tcpChannelId, data);
        } catch (Exception e) {
            log.error("tcpChannelWriteSafe call back error", e);
        }
    }
}

