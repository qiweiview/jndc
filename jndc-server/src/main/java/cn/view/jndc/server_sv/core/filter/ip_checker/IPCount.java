package cn.view.jndc.server_sv.core.filter.ip_checker;

import cn.view.jndc.server_sv.databases_object.IpFilterRecord;

import java.util.concurrent.atomic.AtomicInteger;

public class IPCount {

    private String ip;
    private AtomicInteger count = new AtomicInteger();
    private long lastTimeStamp;


    public IpFilterRecord toIpFilterRecord() {
        IpFilterRecord ipFilterRecord = new IpFilterRecord();
        ipFilterRecord.setIp(ip);
        ipFilterRecord.setVCount(count.get());
        ipFilterRecord.setTimeStamp(lastTimeStamp);
        return ipFilterRecord;
    }

    public IPCount(String ip) {
        this.ip = ip;
    }

    public void increase() {
        count.incrementAndGet();
        timeTag();
    }

    /**
     * record last edit timestamp
     */
    private void timeTag() {
        this.lastTimeStamp = System.currentTimeMillis();
    }

    public void reset() {
        count.set(0);
    }
}
