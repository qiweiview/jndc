package jndc_server.core;

import io.netty.channel.EventLoopGroup;
import jndc.core.NettyComponentConfig;
import jndc.core.UniqueBeanManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ScheduledTaskCenter {
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void start() {

        //记录ip访问日志
        IpChecker ipChecker = UniqueBeanManage.getBean(IpChecker.class);
        eventLoopGroup.scheduleWithFixedDelay(() -> {
            ipChecker.storeRecordData();
        }, 0, 1, TimeUnit.HOURS);


        //检查通道心跳
        NDCServerConfigCenter ndcServerConfigCenter = UniqueBeanManage.getBean(NDCServerConfigCenter.class);
        eventLoopGroup.scheduleWithFixedDelay(() -> {
            ndcServerConfigCenter.checkChannelHealthy();
        }, 0, 5, TimeUnit.MINUTES);




    }

}
