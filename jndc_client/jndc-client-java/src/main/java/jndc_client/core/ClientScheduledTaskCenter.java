package jndc_client.core;

import io.netty.channel.EventLoopGroup;
import jndc.core.NettyComponentConfig;
import jndc.core.UniqueBeanManage;
import jndc_client.core.port_app.ClientServiceProvider;
import jndc_client.core.port_app.ClientTCPDataHandle;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 定时组件
 */
@Slf4j
public class ClientScheduledTaskCenter {
    private EventLoopGroup eventLoopGroup = NettyComponentConfig.getNioEventLoopGroup();


    public void start() {

        JNDCClientConfigCenter bean = UniqueBeanManage.getBean(JNDCClientConfigCenter.class);
        Map<String, ClientServiceProvider> portProtectorMap = bean.getPortProtectorMap();

        //fix the problem of rebind operation fail
        eventLoopGroup.scheduleWithFixedDelay(() -> {

            //循环服务提供者
            portProtectorMap.forEach((k, v) -> {
                Map<String, ClientTCPDataHandle> faceTCPMap = v.getFaceTCPMap();

                //循环连接
                faceTCPMap.forEach((k2, v2) -> {
                    if (v2.isTimeOut()) {
                        //todo 连接超时
                        log.info("release local client cause time out");
                        //释放连接
                        v2.releaseRelatedResources();
                    }
                });

            });

        }, 0, 30, TimeUnit.SECONDS);


    }


}
