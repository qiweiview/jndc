package jndc_server.core;

import io.netty.channel.EventLoopGroup;
import jndc.core.NettyComponentConfig;
import jndc.core.TcpServiceDescription;
import jndc.core.UniqueBeanManage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ScheduledTaskCenter {
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();


    public void start() {
        IpChecker ipChecker = UniqueBeanManage.getBean(IpChecker.class);
        eventLoopGroup.scheduleWithFixedDelay(() -> {
            ipChecker.storeRecordData();
        }, 0, 1, TimeUnit.HOURS);


        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        eventLoopGroup.scheduleWithFixedDelay(() -> {
            ndcServerConfigCenter.checkChannelHealthy();
        }, 0, 5, TimeUnit.MINUTES);


        //fix the problem of rebind operation fail
        eventLoopGroup.scheduleWithFixedDelay(() -> {
            List<TcpServiceDescription> tcpServiceDescriptions = ndcServerConfigCenter.getCurrentSupportService();
            List<TcpServiceDescriptionOnServer> tcpServiceDescriptionOnServers = TcpServiceDescriptionOnServer.ofArray(tcpServiceDescriptions);
            JNDCServerMessageHandle.serviceRebind(tcpServiceDescriptionOnServers);
        }, 0, 3, TimeUnit.MINUTES);


    }


}
