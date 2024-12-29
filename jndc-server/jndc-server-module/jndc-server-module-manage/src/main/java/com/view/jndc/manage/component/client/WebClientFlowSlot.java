package com.view.jndc.manage.component.client;

import com.view.core.client.ndc.flow.ClientFlowSlot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebClientFlowSlot extends ClientFlowSlot {
    @Override
    public void ndcClientStart() {

    }

    @Override
    public void ndcClientStartFail(Exception e) {

    }

    @Override
    protected void connectionActive() {

    }

    @Override
    public void openChannel() {

    }

    @Override
    public void registerTCPService(String serviceId) {

    }

    @Override
    public void unregisterTCPService(String serviceId) {

    }

    @Override
    public void tcpClientStart(String serviceId, String tcpChannelId) {

    }

    @Override
    public void tcpChannelActive(String serviceId, String tcpChannelId) {

    }

    @Override
    public void tcpClientStartFail(String serviceId, String tcpChannelId) {

    }

    @Override
    public void tcpChannelRead(String serviceId, String tcpChannelId, byte[] bytes) {

    }

    @Override
    public void tcpChannelInactive(String serviceId, String tcpChannelId) {

    }

    @Override
    public void tcpChannelWrite(String serviceId, String tcpChannelId, byte[] bytes) {

    }

    @Override
    public void ndcClientInActive() {

    }

    @Override
    public void ndcClientStop() {

    }
}
