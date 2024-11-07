package com.view.core.component.event_bus;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.view.core.component.GlobalBeanContext;
import com.view.core.model.TCPDataTransport;
import com.view.core.model.VirtualTCPService;
import com.view.core.model.event_bus.ChannelOperation;
import com.view.core.model.event_bus.ServiceOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EventListener {


    @AllowConcurrentEvents
    @Subscribe
    public void acceptServiceOperation(ServiceOperation serviceOperation) {
        log.debug("接收到服务注册{}", serviceOperation);
        VirtualTCPService virtualTCPService = serviceOperation.getVirtualTCPService();

        if (serviceOperation.isDeploy()) {
            GlobalBeanContext.APP_CENTER.deployService(virtualTCPService);
        }

    }

    @AllowConcurrentEvents
    @Subscribe
    public void acceptChannelOperation(ChannelOperation channelOperation) {
        String ndcClientId = channelOperation.getNdcClientId();
        if (channelOperation.isInactive()) {
            GlobalBeanContext.APP_CENTER.withdrawRelationalService(ndcClientId);
        }
    }

    @AllowConcurrentEvents
    @Subscribe
    public void acceptChannelOperation(TCPDataTransport tcpDataTransport) {
        GlobalBeanContext.APP_CENTER.receiveData(tcpDataTransport);
    }


}