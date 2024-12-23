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

        VirtualTCPService virtualTCPService = serviceOperation.getVirtualTCPService();

        if (serviceOperation.isDeploy()) {
            log.debug("接收到服务注册{}", serviceOperation);
            GlobalBeanContext.APP_CENTER.deployService(virtualTCPService);
        }

        if (serviceOperation.isWithdraw()) {
            log.debug("接收到服务撤销{}", serviceOperation);
            GlobalBeanContext.APP_CENTER.withdrawService(virtualTCPService);
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
    public void acceptTCPDataTransport(TCPDataTransport tcpDataTransport) {
        GlobalBeanContext.APP_CENTER.receiveData(tcpDataTransport);
    }

    @AllowConcurrentEvents
    @Subscribe
    public void acceptChannelOperation(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            log.warn("异步事件执行失败", e);
        }
    }


}