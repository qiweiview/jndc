package jndc_server.core;

import io.netty.channel.EventLoopGroup;
import jndc.core.NettyComponentConfig;
import jndc.core.UniqueBeanManage;

import java.util.concurrent.TimeUnit;

public class ScheduledTaskCenter {
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();


    public void start(){
        IpChecker ipChecker = UniqueBeanManage.getBean(IpChecker.class);
        eventLoopGroup.scheduleWithFixedDelay(()->{
            ipChecker.storeRecordData();
        },0,1, TimeUnit.HOURS);
    }


}
