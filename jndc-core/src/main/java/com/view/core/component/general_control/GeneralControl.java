package com.view.core.component.general_control;

import com.view.core.component.GlobalBeanContext;
import com.view.core.component.general_control.plugins.NetworkTrafficAnalyser;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * 总控
 */
@Slf4j
public class GeneralControl {

    //流量统计
    private Map<String, NetworkTrafficAnalyser> serviceIdMap = new ConcurrentHashMap<>();

    public GeneralControl() {
        initHealthyChecker();
    }

    /**
     * 添加流量
     *
     * @param ndcClientId
     * @param traffic
     */
    public void addTraffic(String ndcClientId, int traffic) {
        //todo 累加流量
        try {
            NetworkTrafficAnalyser networkTrafficAnalyser = serviceIdMap.get(ndcClientId);
            if (networkTrafficAnalyser == null) {
                networkTrafficAnalyser = new NetworkTrafficAnalyser(ndcClientId);
                serviceIdMap.put(ndcClientId, networkTrafficAnalyser);
            }
            networkTrafficAnalyser.addTraffic(traffic);
        } catch (Exception e) {
            //todo 不影响正常流量
            log.error("累加流量失败", e);
        }
    }


    private void initHealthyChecker() {

        //每分钟检查一次
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            GlobalBeanContext.APP_CENTER.getTcpServerMap().forEach((k, server) -> {
                //todo 检查服务是否健康
                server.checkHealthy();
            });
        }, 0, 1, java.util.concurrent.TimeUnit.MINUTES);
        log.info("健康检查器已启动，频率为一分钟一次");
    }
}
