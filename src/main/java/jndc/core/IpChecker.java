package jndc.core;

import jndc.server.IpFilterRule4V;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;

public class IpChecker {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private volatile LinkedBlockingQueue<IpRecord> recordQueue = new LinkedBlockingQueue<>();

    private Map<String, IpFilterRule4V> blackMap = new HashMap<>();

    private Map<String, IpFilterRule4V> whiteMap = new HashMap<>();//higher priority

    private ExecutorService executorService;

    private volatile Map<String, List<IpRecord>> releaseMap = new HashMap<>();//only operated by one thread

    private volatile Map<String, List<IpRecord>> blockMap = new HashMap<>();//only operated by one thread

    private volatile boolean work = true;

    private volatile boolean pause = false;


    public IpChecker() {
        executorService = Executors.newFixedThreadPool(1);
        executorService.submit(() -> {
            while (work) {
                if (!pause) {
                    try {
                        IpRecord take = recordQueue.take();
                        if (take.isRelease()) {
                            addToMap(take, releaseMap);
                        } else {
                            addToMap(take, blockMap);
                        }
                    } catch (InterruptedException e) {
                        logger.error("recordQueue：" + e);
                    }
                }
            }
        });
    }


    private void addToMap(IpRecord take, Map<String, List<IpRecord>> map) {
        String ip = take.getIp();
        List<IpRecord> ipRecords = map.get(ip);
        if (ipRecords == null) {
            ipRecords = new ArrayList<>();
            map.put(ip, ipRecords);
        }
        ipRecords.add(take);
    }

    public void loadRule(Map<String, IpFilterRule4V> blackMap, Map<String, IpFilterRule4V> whiteMap) {
        this.blackMap =blackMap;
        this.whiteMap =whiteMap;
    }


    public boolean checkIpAddress(String ipAddress) {
        if (whiteMap.size() > 0) {
            if (whiteMap.containsKey(ipAddress)) {
                try {
                    recordQueue.put(new IpRecord(ipAddress, 0));
                } catch (InterruptedException e) {
                    logger.error("releaseQueue：" + e);
                }
                return true;
            }else {
                try {
                    recordQueue.put(new IpRecord(ipAddress, 1));
                } catch (InterruptedException e) {
                    logger.error("blockQueue：" + e);
                }
                return false;
            }
        }


        //do not perform blacklist matching if there is a whitelist
        if (blackMap.containsKey(ipAddress)) {
            try {
                recordQueue.put(new IpRecord(ipAddress, 1));
            } catch (InterruptedException e) {
                logger.error("blockQueue：" + e);
            }
            return false;
        } else {
            try {
                recordQueue.put(new IpRecord(ipAddress, 0));
            } catch (InterruptedException e) {
                logger.error("releaseQueue：" + e);
            }
            return true;
        }
    }

    /* --------------getter and setter-------------- */

    public Map<String, List<IpRecord>> getReleaseMap() {
        return releaseMap;
    }

    public Map<String, List<IpRecord>> getBlockMap() {
        return blockMap;
    }

    public Map<String, IpFilterRule4V> getBlackMap() {
        return blackMap;
    }

    public Map<String, IpFilterRule4V> getWhiteMap() {
        return whiteMap;
    }

    public class IpRecord {
        private String ip;
        private int tag;//0 release 1 block
        private long time;


        public boolean isRelease() {
            return tag == 0;
        }

        @Override
        public String toString() {
            return time + ":" + ip;
        }

        public IpRecord(String ip, int tag) {
            this.ip = ip;
            this.tag = tag;
            this.time = System.currentTimeMillis();
        }


        /* --------------getter and setter-------------- */



        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }
    }


}
