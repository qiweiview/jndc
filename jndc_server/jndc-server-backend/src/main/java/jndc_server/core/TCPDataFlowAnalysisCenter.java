package jndc_server.core;

import jndc.core.NDCMessageProtocol;
import jndc.core.data_store_support.DBWrapper;
import jndc_server.databases_object.ClientAuthRecord;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流量分析中心
 */
@Data
@Slf4j
public class TCPDataFlowAnalysisCenter {

    public static final String DIRECTION_CLIENT_TO_SERVER = "CLIENT_TO_SERVER";

    public static final String DIRECTION_SERVER_TO_CLIENT = "SERVER_TO_CLIENT";

    private static final long BANDWIDTH_WINDOW_SECONDS = 5L;

    private static final long FLUSH_INTERVAL_MILLIS = 1000L;

    private final AsynchronousEventCenter asynchronousEventCenter;

    private final Map<String, DeviceTrafficRuntime> runtimeMap = new ConcurrentHashMap<>();

    public TCPDataFlowAnalysisCenter(AsynchronousEventCenter asynchronousEventCenter) {
        this.asynchronousEventCenter = asynchronousEventCenter;
    }

    public void analyse(String clientId, NDCMessageProtocol data, String direction) {
        analyse(clientId, data, direction, System.currentTimeMillis());
    }

    void analyse(String clientId, NDCMessageProtocol data, String direction, long nowMillis) {
        if (clientId == null || "".equals(clientId.trim()) || data == null || data.getType() != NDCMessageProtocol.TCP_DATA) {
            return;
        }
        byte[] payload = data.getData();
        int bytes = payload == null ? 0 : payload.length;
        if (bytes <= 0) {
            return;
        }

        DeviceTrafficRuntime runtime = runtimeMap.computeIfAbsent(clientId, x -> new DeviceTrafficRuntime());
        boolean needFlush = false;
        synchronized (runtime) {
            runtime.record(direction, bytes, nowMillis);
            if (runtime.shouldScheduleFlush(nowMillis)) {
                runtime.markFlushScheduled(nowMillis);
                needFlush = true;
            }
        }

        if (needFlush) {
            asynchronousEventCenter.dataAnalyseJob(() -> flush(clientId, runtime));
        }
    }

    public TrafficSnapshot getTrafficSnapshot(String clientId, boolean online) {
        return getTrafficSnapshot(clientId, online, System.currentTimeMillis());
    }

    TrafficSnapshot getTrafficSnapshot(String clientId, boolean online, long nowMillis) {
        if (clientId == null || "".equals(clientId.trim())) {
            return TrafficSnapshot.empty();
        }
        DeviceTrafficRuntime runtime = runtimeMap.get(clientId);
        if (runtime == null) {
            return TrafficSnapshot.empty();
        }

        synchronized (runtime) {
            runtime.pruneBuckets(nowMillis);
            long clientToServerBandwidth = online ? runtime.calculateBandwidth(DIRECTION_CLIENT_TO_SERVER) : 0L;
            long serverToClientBandwidth = online ? runtime.calculateBandwidth(DIRECTION_SERVER_TO_CLIENT) : 0L;
            return new TrafficSnapshot(
                    runtime.getPendingClientToServerBytes(),
                    runtime.getPendingServerToClientBytes(),
                    clientToServerBandwidth,
                    serverToClientBandwidth,
                    runtime.getLastUpdatedAt()
            );
        }
    }

    private void flush(String clientId, DeviceTrafficRuntime runtime) {
        long clientToServerBytes;
        long serverToClientBytes;
        synchronized (runtime) {
            clientToServerBytes = runtime.getPendingClientToServerBytes();
            serverToClientBytes = runtime.getPendingServerToClientBytes();
            runtime.setPendingClientToServerBytes(0L);
            runtime.setPendingServerToClientBytes(0L);
        }

        try {
            if (clientToServerBytes > 0 || serverToClientBytes > 0) {
                DBWrapper<ClientAuthRecord> dbWrapper = DBWrapper.getDBWrapper(ClientAuthRecord.class);
                ClientAuthRecord clientAuthRecord = loadOrCreateClientRecord(dbWrapper, clientId);
                clientAuthRecord.setClientToServerBytes(safeAdd(clientAuthRecord.getClientToServerBytes(), clientToServerBytes));
                clientAuthRecord.setServerToClientBytes(safeAdd(clientAuthRecord.getServerToClientBytes(), serverToClientBytes));
                dbWrapper.updateByPrimaryKey(clientAuthRecord);
            }
        } catch (RuntimeException e) {
            log.error("flush traffic stats failed for client {}", clientId, e);
            synchronized (runtime) {
                runtime.setPendingClientToServerBytes(runtime.getPendingClientToServerBytes() + clientToServerBytes);
                runtime.setPendingServerToClientBytes(runtime.getPendingServerToClientBytes() + serverToClientBytes);
            }
        } finally {
            synchronized (runtime) {
                runtime.setFlushScheduled(false);
            }
        }
    }

    private ClientAuthRecord loadOrCreateClientRecord(DBWrapper<ClientAuthRecord> dbWrapper, String clientId) {
        ClientAuthRecord clientAuthRecord = dbWrapper.customQuerySingle(
                "select * from client_auth_record where client_id=?",
                clientId
        );
        if (clientAuthRecord != null) {
            return clientAuthRecord;
        }

        clientAuthRecord = new ClientAuthRecord();
        clientAuthRecord.setClientId(clientId);
        clientAuthRecord.setClientAuthKey("");
        clientAuthRecord.setClientToServerBytes(0L);
        clientAuthRecord.setServerToClientBytes(0L);
        dbWrapper.insert(clientAuthRecord);
        return dbWrapper.customQuerySingle("select * from client_auth_record where client_id=?", clientId);
    }

    private long safeAdd(Long base, long delta) {
        long left = base == null ? 0L : base;
        return left + delta;
    }

    @Data
    public static class TrafficSnapshot {
        private final long pendingClientToServerBytes;
        private final long pendingServerToClientBytes;
        private final long clientToServerBandwidth;
        private final long serverToClientBandwidth;
        private final long trafficUpdatedAt;

        public static TrafficSnapshot empty() {
            return new TrafficSnapshot(0L, 0L, 0L, 0L, 0L);
        }
    }

    @Data
    private static class DeviceTrafficRuntime {
        private final Map<Long, TrafficBucket> secondBuckets = new HashMap<>();
        private long pendingClientToServerBytes;
        private long pendingServerToClientBytes;
        private long lastUpdatedAt;
        private long lastFlushAt;
        private boolean flushScheduled;

        public void record(String direction, int bytes, long nowMillis) {
            pruneBuckets(nowMillis);

            long nowSecond = nowMillis / 1000;
            TrafficBucket trafficBucket = secondBuckets.get(nowSecond);
            if (trafficBucket == null) {
                trafficBucket = new TrafficBucket();
                secondBuckets.put(nowSecond, trafficBucket);
            }

            if (DIRECTION_CLIENT_TO_SERVER.equals(direction)) {
                trafficBucket.setClientToServerBytes(trafficBucket.getClientToServerBytes() + bytes);
                pendingClientToServerBytes += bytes;
            } else if (DIRECTION_SERVER_TO_CLIENT.equals(direction)) {
                trafficBucket.setServerToClientBytes(trafficBucket.getServerToClientBytes() + bytes);
                pendingServerToClientBytes += bytes;
            } else {
                return;
            }

            lastUpdatedAt = nowMillis;
        }

        public boolean shouldScheduleFlush(long nowMillis) {
            return !flushScheduled && nowMillis - lastFlushAt >= FLUSH_INTERVAL_MILLIS;
        }

        public void markFlushScheduled(long nowMillis) {
            flushScheduled = true;
            lastFlushAt = nowMillis;
        }

        public void pruneBuckets(long nowMillis) {
            long minSecond = nowMillis / 1000 - (BANDWIDTH_WINDOW_SECONDS - 1);
            Iterator<Map.Entry<Long, TrafficBucket>> iterator = secondBuckets.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Long, TrafficBucket> entry = iterator.next();
                if (entry.getKey() < minSecond) {
                    iterator.remove();
                }
            }
        }

        public long calculateBandwidth(String direction) {
            long totalBytes = 0L;
            for (TrafficBucket trafficBucket : secondBuckets.values()) {
                totalBytes += DIRECTION_CLIENT_TO_SERVER.equals(direction)
                        ? trafficBucket.getClientToServerBytes()
                        : trafficBucket.getServerToClientBytes();
            }
            return totalBytes / BANDWIDTH_WINDOW_SECONDS;
        }
    }

    @Data
    private static class TrafficBucket {
        private long clientToServerBytes;
        private long serverToClientBytes;
    }
}
