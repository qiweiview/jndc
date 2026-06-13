package jndc_server.core;

import io.netty.channel.EventLoopGroup;
import jndc.core.NettyComponentConfig;
import jndc.core.UniqueBeanManage;
import jndc_server.core.filter.IpChecker;

import java.util.concurrent.TimeUnit;

/**
 * 定时任务中心
 */
public class ScheduledTaskCenter {
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();


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


        RuntimeDataCleanupService runtimeDataCleanupService = UniqueBeanManage.getBean(RuntimeDataCleanupService.class);
        AsynchronousEventCenter asynchronousEventCenter = UniqueBeanManage.getBean(AsynchronousEventCenter.class);
        if (runtimeDataCleanupService != null && asynchronousEventCenter != null) {
            asynchronousEventCenter.dbJob(runtimeDataCleanupService::cleanupExpiredData);
            long intervalHours = runtimeDataCleanupService.getRunIntervalHours();
            eventLoopGroup.scheduleWithFixedDelay(() -> {
                asynchronousEventCenter.dbJob(runtimeDataCleanupService::cleanupExpiredData);
            }, intervalHours, intervalHours, TimeUnit.HOURS);
        }


    }

}
