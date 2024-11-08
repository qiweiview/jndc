package com.view.core.component.general_control.plugins;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetworkTrafficAnalyser {
    private String ndcClientId;
    private long lastActiveTime;

    private volatile double totalKB;
    private volatile double totalMB;
    private volatile double totalGB;
    private volatile double totalTB;

    public NetworkTrafficAnalyser(String ndcClientId) {
        this.ndcClientId = ndcClientId;
    }

    /**
     * 累加流量
     *
     * @param traffic 单位为字节
     */
    public void addTraffic(int traffic) {
        //转换为KB
        totalKB += traffic / 1024d;

        //进位
        carry();

        //更新时间
        lastActiveTime = System.currentTimeMillis();
    }

    private void carry() {
        if (totalKB > 1024) {
            totalMB += totalKB / 1024d;
            totalKB = totalKB % 1024;
        }
        if (totalMB > 1024) {
            totalGB += totalMB / 1024d;
            totalMB = totalMB % 1024;
        }
        if (totalGB > 1024) {
            totalTB += totalGB / 1024d;
            totalGB = totalGB % 1024;
        }
    }

    public String formatTraffic() {
        return String.format("总流量为：%.2fTB,%.2fGB,%.2fMB,%.2fKB", totalTB, totalGB, totalMB, totalKB);
    }
}

