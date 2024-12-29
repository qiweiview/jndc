package com.view.core.server.ndc.flow;

import java.net.InetSocketAddress;

public class EmptyServerFlowSlot extends ServerFlowSlot {

    @Override
    public void ndcServerStart() {

    }

    @Override
    public void ndcServerStartFail(Exception e) {

    }

    @Override
    public void connectActive() {

    }

    @Override
    public void openChannel(String clientId) {

    }

    @Override
    public void tcpServerStartSuccess(String ndcClientId, String serviceId) {

    }

    @Override
    public void tcpServerStartFail(String ndcClientId, String serviceId) {

    }

    @Override
    public void tcpChannelActive(String ndcClientId, String serviceId, String tcpChannelId) {

    }

    @Override
    public void tcpChannelRead(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress remote, byte[] data) {

    }

    @Override
    public void tcpChannelWrite(String ndcClientId, String serviceId, String tcpChannelId, byte[] data) {

    }

    @Override
    public void tcpChannelInactive(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress remote) {

    }

    @Override
    public void tcpServerStop(String ndcClientId, String serviceId) {

    }

    @Override
    public void connectInActive(String clientId) {

    }

    @Override
    public void ndcServerStop() {

    }
}
