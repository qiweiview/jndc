package com.view.core.component.general_control.plugins.ip_blocker;

import lombok.Data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * IP阻断器
 */
@Data
public class IPBlocker {

    //阻断IP集合
    private Set<String> blockIPSet = new HashSet<>();

    //允许IP集合
    private Set<String> allowIPSet = new HashSet<>();

    private Map<String, IPRecord> ipPool = new ConcurrentHashMap();

    public void addBlockIP(String ip) {
        blockIPSet.add(ip);
    }

    public void addAllowIP(String ip) {
        allowIPSet.add(ip);
    }

    /**
     * 检查IP是否被阻断
     *
     * @param ip
     * @return
     */
    public boolean checkBlock(String ip) {
        //添加到IP池
        increaseIPRecord(ip);


        ///优先级最高
        if (allowIPSet.contains(ip)) {
            return false;
        }

        return blockIPSet.contains(ip);
    }

    /**
     * 增加IP访问记录
     *
     * @param ip
     */
    private void increaseIPRecord(String ip) {
        IPRecord ipRecord = ipPool.get(ip);
        if (ipRecord == null) {
            ipRecord = new IPRecord();
            ipRecord.setIp(ip);
            ipRecord.setLastActiveTime(System.currentTimeMillis());
            ipRecord.setTotalTraffic(0);
            ipPool.put(ip, ipRecord);
        }
        ipRecord.setLastActiveTime(System.currentTimeMillis());
        ipRecord.totalTrafficIncrease();
    }


}
