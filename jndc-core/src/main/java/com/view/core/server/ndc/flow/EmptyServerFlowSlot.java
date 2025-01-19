package com.view.core.server.ndc.flow;

import com.view.core.server.tcp.TCPServerConfiguration;

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
    public void openChannel(String clientId, InetSocketAddress remote) {

    }

    @Override
    public void tcpServerStartSuccess(TCPServerConfiguration tcpServerConfiguration) {

    }

    @Override
    public void tcpServerStartFail(String ndcClientId, String serviceId) {

    }

    @Override
    public void tcpChannelActive(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress tcpRemote) {

    }

    @Override
    public void tcpChannelRead(String ndcClientId, String serviceId, String tcpChannelId, InetSocketAddress remote, byte[] data) {

    }

    @Override
    public void serviceRegister(String serverId, String ndcClientId, String serviceId) {

    }

    @Override
    public void serviceUnRegister(String serverId, String ndcClientId, String serviceId) {

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
    protected void clientHeartBeat(String ndcClientId, long timestamp) {

    }

    @Override
    public void ndcServerStop() {

    }
}
