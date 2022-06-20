package jndc_server.core.filter;

import jndc.core.data_store_support.DBWrapper;
import jndc.utils.UUIDSimple;
import jndc_server.databases_object.IpFilterRecord;
import jndc_server.databases_object.IpFilterRule4V;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class IpChecker {

    private  LinkedBlockingQueue<IpRecord> recordQueue = new LinkedBlockingQueue<>();

    private Map<String, IpFilterRule4V> blackMap = new HashMap<>();

    private Map<String, IpFilterRule4V> whiteMap = new HashMap<>();//higher priority

    private ExecutorService executorService;

    //release ip map
    private  Map<String, IPCount> releaseMap = new ConcurrentHashMap<>();

    //block ip map
    private  Map<String, IPCount> blockMap = new ConcurrentHashMap<>();

    private volatile boolean work = true;

    private volatile boolean pause = false;

    private final long IP_CACHE_EXPIRE=24*60*60*1000L;


    public void storeRecordData() {
        DBWrapper<IpFilterRecord> dbWrapper = DBWrapper.getDBWrapper(IpFilterRecord.class);
        List<IpFilterRecord> list = new ArrayList<>();
        releaseMap.forEach((k, v) -> {
            IpFilterRecord ipFilterRecord = v.toIpFilterRecord();
            if (ipFilterRecord.getVCount() > 0) {
                ipFilterRecord.setRecordType(IpRecord.RELEASE_STATE);
                ipFilterRecord.setId(UUIDSimple.id());
                list.add(ipFilterRecord);
                v.reset();
            } else {
                //todo remove expire key
                long timeStamp = ipFilterRecord.getTimeStamp();
                if (timeStamp + IP_CACHE_EXPIRE < System.currentTimeMillis()) {
                    releaseMap.remove(k);
                }
            }
        });

        blockMap.forEach((k, v) -> {
            IpFilterRecord ipFilterRecord = v.toIpFilterRecord();
            if (ipFilterRecord.getVCount() > 0) {
                ipFilterRecord.setRecordType(IpRecord.BLOCK_STATE);
                ipFilterRecord.setId(UUIDSimple.id());
                list.add(ipFilterRecord);
                v.reset();
            } else {
                //todo remove expire key
                long timeStamp = ipFilterRecord.getTimeStamp();
                if (timeStamp + IP_CACHE_EXPIRE < System.currentTimeMillis()) {
                    blockMap.remove(k);
                }
            }
        });

        dbWrapper.insertBatch(list);

    }

    public IpChecker() {
        executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            while (work) {
                if (!pause) {
                    try {
                        IpRecord take = recordQueue.take();
                        if (take.isRelease()) {//check the type of record
                            addToMap(take, releaseMap);
                        } else {
                            addToMap(take, blockMap);
                        }
                    } catch (InterruptedException e) {
                        log.error("recordQueue：" + e);
                    }
                }
            }
        });
    }


    private void addToMap(IpRecord take, Map<String, IPCount> map) {
        String ip = take.getIp();
        IPCount ipCount = map.get(ip);
        if (ipCount == null) {
            ipCount = new IPCount(ip);
            map.put(ip, ipCount);
        }
        ipCount.increase();
    }

    public void loadRule(Map<String, IpFilterRule4V> blackMap, Map<String, IpFilterRule4V> whiteMap) {
        this.blackMap = blackMap;
        this.whiteMap = whiteMap;
    }


    public boolean checkIpAddress(String ipAddress) {

        //if ip white is not empty
        if (whiteMap.size() > 0) {
            if (whiteMap.containsKey(ipAddress)) {

                //do logging
                try {
                    recordQueue.put(new IpRecord(ipAddress, IpRecord.RELEASE_STATE));
                } catch (InterruptedException e) {
                    log.error("releaseQueue：" + e);
                }

                //release request
                return true;
            } else {

                //do logging
                try {
                    recordQueue.put(new IpRecord(ipAddress, IpRecord.BLOCK_STATE));
                } catch (InterruptedException e) {
                    log.error("blockQueue：" + e);
                }

                //block request
                return false;
            }
        }


        //do not perform blacklist matching if there is a whitelist
        if (blackMap.containsKey(ipAddress)) {
            //do logging
            try {
                recordQueue.put(new IpRecord(ipAddress, IpRecord.BLOCK_STATE));
            } catch (InterruptedException e) {
                log.error("blockQueue：" + e);
            }

            //block request
            return false;
        } else {
            //do logging
            try {
                recordQueue.put(new IpRecord(ipAddress, IpRecord.RELEASE_STATE));
            } catch (InterruptedException e) {
                log.error("releaseQueue：" + e);
            }

            //release request
            return true;
        }
    }

    /* --------------getter and setter-------------- */


    public Map<String, IpFilterRule4V> getBlackMap() {
        return blackMap;
    }

    public Map<String, IpFilterRule4V> getWhiteMap() {
        return whiteMap;
    }


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

    public class IpRecord {

        public static final int RELEASE_STATE = 0;
        public static final int BLOCK_STATE = 1;

        private String ip;

        private int tag;//0 release 1 block


        public boolean isRelease() {
            return tag == 0;
        }


        public IpRecord(String ip, int tag) {
            this.ip = ip;
            this.tag = tag;
        }


        /* --------------getter and setter-------------- */

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

    }


}
