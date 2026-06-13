package jndc_server.core;

import jndc.core.NDCMessageProtocol;
import jndc.core.data_store_support.DBWrapper;
import jndc_server.databases_object.ClientAuthRecord;
import jndc_server.databases_object.ClientTrafficTrendRecord;
import jndc_server.web_support.model.vo.ChannelTrafficTrendPointVO;
import jndc_server.web_support.model.vo.ChannelTrafficTrendVO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    private static final String BUCKET_TYPE_MINUTE = "MINUTE";

    private static final String BUCKET_TYPE_HOUR = "HOUR";

    private static final String BUCKET_TYPE_DAY = "DAY";

    private static final String BUCKET_TYPE_MONTH = "MONTH";

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
        Map<String, TrendBucketDelta> pendingTrendBuckets;
        synchronized (runtime) {
            clientToServerBytes = runtime.getPendingClientToServerBytes();
            serverToClientBytes = runtime.getPendingServerToClientBytes();
            pendingTrendBuckets = runtime.copyPendingTrendBuckets();
            runtime.setPendingClientToServerBytes(0L);
            runtime.setPendingServerToClientBytes(0L);
            runtime.clearPendingTrendBuckets();
        }

        try {
            if (clientToServerBytes > 0 || serverToClientBytes > 0) {
                DBWrapper<ClientAuthRecord> dbWrapper = DBWrapper.getDBWrapper(ClientAuthRecord.class);
                ClientAuthRecord clientAuthRecord = loadOrCreateClientRecord(dbWrapper, clientId);
                clientAuthRecord.setClientToServerBytes(safeAdd(clientAuthRecord.getClientToServerBytes(), clientToServerBytes));
                clientAuthRecord.setServerToClientBytes(safeAdd(clientAuthRecord.getServerToClientBytes(), serverToClientBytes));
                dbWrapper.updateByPrimaryKey(clientAuthRecord);
                updateTrendRecords(clientId, pendingTrendBuckets);
            }
        } catch (RuntimeException e) {
            log.error("flush traffic stats failed for client {}", clientId, e);
            synchronized (runtime) {
                runtime.setPendingClientToServerBytes(runtime.getPendingClientToServerBytes() + clientToServerBytes);
                runtime.setPendingServerToClientBytes(runtime.getPendingServerToClientBytes() + serverToClientBytes);
                runtime.mergePendingTrendBuckets(pendingTrendBuckets);
            }
        } finally {
            synchronized (runtime) {
                runtime.setFlushScheduled(false);
            }
        }
    }

    public ChannelTrafficTrendVO getTrafficTrend(String clientId, String range) {
        return getTrafficTrend(clientId, range, System.currentTimeMillis());
    }

    ChannelTrafficTrendVO getTrafficTrend(String clientId, String range, long nowMillis) {
        TrendRangeDefinition definition = TrendRangeDefinition.of(range);
        ChannelTrafficTrendVO result = new ChannelTrafficTrendVO();
        result.setRange(definition.getApiValue());
        result.setBucketUnit(definition.getBucketUnit());
        if (clientId == null || "".equals(clientId.trim())) {
            fillEmptyPoints(result, definition, nowMillis);
            return result;
        }

        DBWrapper<ClientTrafficTrendRecord> dbWrapper = DBWrapper.getDBWrapper(ClientTrafficTrendRecord.class);
        long minBucketStartAt = definition.firstBucketStart(nowMillis);
        List<ClientTrafficTrendRecord> records = dbWrapper.customQuery(
                "select * from client_traffic_trend_record where client_id=? and bucket_type=? and bucket_start_at>=? order by bucket_start_at asc",
                clientId,
                definition.getSourceBucketType(),
                minBucketStartAt
        );

        Map<Long, ClientTrafficTrendRecord> recordMap = new HashMap<Long, ClientTrafficTrendRecord>();
        for (ClientTrafficTrendRecord record : records) {
            recordMap.put(record.getBucketStartAt(), record);
        }

        List<ChannelTrafficTrendPointVO> points = new ArrayList<ChannelTrafficTrendPointVO>();
        long bucketStartAt = minBucketStartAt;
        for (int i = 0; i < definition.getPointCount(); i++) {
            ClientTrafficTrendRecord record = recordMap.get(bucketStartAt);
            long clientToServerBytes = record == null || record.getClientToServerBytes() == null ? 0L : record.getClientToServerBytes();
            long serverToClientBytes = record == null || record.getServerToClientBytes() == null ? 0L : record.getServerToClientBytes();
            points.add(new ChannelTrafficTrendPointVO(
                    bucketStartAt,
                    clientToServerBytes,
                    serverToClientBytes,
                    clientToServerBytes + serverToClientBytes
            ));
            bucketStartAt = definition.nextBucketStart(bucketStartAt);
        }
        result.setPoints(points);
        return result;
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

    private void updateTrendRecords(String clientId, Map<String, TrendBucketDelta> pendingTrendBuckets) {
        DBWrapper<ClientTrafficTrendRecord> dbWrapper = DBWrapper.getDBWrapper(ClientTrafficTrendRecord.class);
        for (TrendBucketDelta delta : pendingTrendBuckets.values()) {
            upsertTrendRecord(
                    dbWrapper,
                    clientId,
                    delta.getBucketType(),
                    delta.getBucketStartAt(),
                    delta.getClientToServerBytes(),
                    delta.getServerToClientBytes(),
                    delta.getUpdatedAt()
            );
        }
    }

    private void upsertTrendRecord(
            DBWrapper<ClientTrafficTrendRecord> dbWrapper,
            String clientId,
            String bucketType,
            long bucketStartAt,
            long clientToServerBytes,
            long serverToClientBytes,
            long updatedAt
    ) {
        String id = buildTrendRecordId(clientId, bucketType, bucketStartAt);
        ClientTrafficTrendRecord record = dbWrapper.customQuerySingle(
                "select * from client_traffic_trend_record where id=?",
                id
        );
        if (record == null) {
            record = new ClientTrafficTrendRecord();
            record.setId(id);
            record.setClientId(clientId);
            record.setBucketType(bucketType);
            record.setBucketStartAt(bucketStartAt);
            record.setClientToServerBytes(clientToServerBytes);
            record.setServerToClientBytes(serverToClientBytes);
            record.setUpdatedAt(updatedAt);
            dbWrapper.insert(record);
            return;
        }

        record.setClientToServerBytes(safeAdd(record.getClientToServerBytes(), clientToServerBytes));
        record.setServerToClientBytes(safeAdd(record.getServerToClientBytes(), serverToClientBytes));
        record.setUpdatedAt(updatedAt);
        dbWrapper.updateByPrimaryKey(record);
    }

    private String buildTrendRecordId(String clientId, String bucketType, long bucketStartAt) {
        return clientId + "_" + bucketType + "_" + bucketStartAt;
    }

    private void fillEmptyPoints(ChannelTrafficTrendVO result, TrendRangeDefinition definition, long nowMillis) {
        List<ChannelTrafficTrendPointVO> points = new ArrayList<ChannelTrafficTrendPointVO>();
        long bucketStartAt = definition.firstBucketStart(nowMillis);
        for (int i = 0; i < definition.getPointCount(); i++) {
            points.add(new ChannelTrafficTrendPointVO(bucketStartAt, 0L, 0L, 0L));
            bucketStartAt = definition.nextBucketStart(bucketStartAt);
        }
        result.setPoints(points);
    }

    private static long truncateToMinute(long millis) {
        return toTime(millis).truncatedTo(ChronoUnit.MINUTES).toInstant().toEpochMilli();
    }

    private static long truncateToHour(long millis) {
        return toTime(millis).truncatedTo(ChronoUnit.HOURS).toInstant().toEpochMilli();
    }

    private static long truncateToDay(long millis) {
        return toTime(millis).truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli();
    }

    private static long truncateToMonth(long millis) {
        ZonedDateTime time = toTime(millis);
        return time.withDayOfMonth(1).truncatedTo(ChronoUnit.DAYS).toInstant().toEpochMilli();
    }

    private static ZonedDateTime toTime(long millis) {
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault());
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
        private final Map<String, TrendBucketDelta> pendingTrendBuckets = new HashMap<String, TrendBucketDelta>();
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

            addTrendDelta(BUCKET_TYPE_MINUTE, truncateToMinute(nowMillis), direction, bytes, nowMillis);
            addTrendDelta(BUCKET_TYPE_HOUR, truncateToHour(nowMillis), direction, bytes, nowMillis);
            addTrendDelta(BUCKET_TYPE_DAY, truncateToDay(nowMillis), direction, bytes, nowMillis);
            addTrendDelta(BUCKET_TYPE_MONTH, truncateToMonth(nowMillis), direction, bytes, nowMillis);
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

        public Map<String, TrendBucketDelta> copyPendingTrendBuckets() {
            Map<String, TrendBucketDelta> copied = new HashMap<String, TrendBucketDelta>();
            for (Map.Entry<String, TrendBucketDelta> entry : pendingTrendBuckets.entrySet()) {
                copied.put(entry.getKey(), entry.getValue().copy());
            }
            return copied;
        }

        public void clearPendingTrendBuckets() {
            pendingTrendBuckets.clear();
        }

        public void mergePendingTrendBuckets(Map<String, TrendBucketDelta> trendBuckets) {
            for (TrendBucketDelta delta : trendBuckets.values()) {
                TrendBucketDelta existing = pendingTrendBuckets.get(delta.key());
                if (existing == null) {
                    pendingTrendBuckets.put(delta.key(), delta.copy());
                    continue;
                }
                existing.setClientToServerBytes(existing.getClientToServerBytes() + delta.getClientToServerBytes());
                existing.setServerToClientBytes(existing.getServerToClientBytes() + delta.getServerToClientBytes());
                existing.setUpdatedAt(Math.max(existing.getUpdatedAt(), delta.getUpdatedAt()));
            }
        }

        private void addTrendDelta(String bucketType, long bucketStartAt, String direction, int bytes, long updatedAt) {
            String key = bucketType + "_" + bucketStartAt;
            TrendBucketDelta delta = pendingTrendBuckets.get(key);
            if (delta == null) {
                delta = new TrendBucketDelta(bucketType, bucketStartAt);
                pendingTrendBuckets.put(key, delta);
            }
            if (DIRECTION_CLIENT_TO_SERVER.equals(direction)) {
                delta.setClientToServerBytes(delta.getClientToServerBytes() + bytes);
            } else if (DIRECTION_SERVER_TO_CLIENT.equals(direction)) {
                delta.setServerToClientBytes(delta.getServerToClientBytes() + bytes);
            }
            delta.setUpdatedAt(Math.max(delta.getUpdatedAt(), updatedAt));
        }
    }

    @Data
    private static class TrafficBucket {
        private long clientToServerBytes;
        private long serverToClientBytes;
    }

    @Data
    private static class TrendBucketDelta {
        private final String bucketType;
        private final long bucketStartAt;
        private long clientToServerBytes;
        private long serverToClientBytes;
        private long updatedAt;

        private TrendBucketDelta(String bucketType, long bucketStartAt) {
            this.bucketType = bucketType;
            this.bucketStartAt = bucketStartAt;
        }

        private String key() {
            return bucketType + "_" + bucketStartAt;
        }

        private TrendBucketDelta copy() {
            TrendBucketDelta copied = new TrendBucketDelta(bucketType, bucketStartAt);
            copied.setClientToServerBytes(clientToServerBytes);
            copied.setServerToClientBytes(serverToClientBytes);
            copied.setUpdatedAt(updatedAt);
            return copied;
        }
    }

    private static class TrendRangeDefinition {
        private final String apiValue;
        private final String sourceBucketType;
        private final String bucketUnit;
        private final int pointCount;

        private TrendRangeDefinition(String apiValue, String sourceBucketType, String bucketUnit, int pointCount) {
            this.apiValue = apiValue;
            this.sourceBucketType = sourceBucketType;
            this.bucketUnit = bucketUnit;
            this.pointCount = pointCount;
        }

        public String getApiValue() {
            return apiValue;
        }

        public String getSourceBucketType() {
            return sourceBucketType;
        }

        public String getBucketUnit() {
            return bucketUnit;
        }

        public int getPointCount() {
            return pointCount;
        }

        static TrendRangeDefinition of(String range) {
            if ("1hour".equals(range)) {
                return new TrendRangeDefinition("1hour", BUCKET_TYPE_MINUTE, "minute", 60);
            }
            if ("7day".equals(range)) {
                return new TrendRangeDefinition("7day", BUCKET_TYPE_DAY, "day", 7);
            }
            if ("1month".equals(range)) {
                return new TrendRangeDefinition("1month", BUCKET_TYPE_DAY, "day", 30);
            }
            if ("1year".equals(range)) {
                return new TrendRangeDefinition("1year", BUCKET_TYPE_MONTH, "month", 12);
            }
            return new TrendRangeDefinition("24hour", BUCKET_TYPE_HOUR, "hour", 24);
        }

        long firstBucketStart(long nowMillis) {
            if ("month".equals(bucketUnit)) {
                return shiftMonths(truncateMonthStart(nowMillis), -(pointCount - 1));
            }
            if ("day".equals(bucketUnit)) {
                return truncateDayStart(nowMillis) - (pointCount - 1) * ChronoUnit.DAYS.getDuration().toMillis();
            }
            if ("hour".equals(bucketUnit)) {
                return truncateHourStart(nowMillis) - (pointCount - 1) * ChronoUnit.HOURS.getDuration().toMillis();
            }
            if ("minute".equals(bucketUnit)) {
                return truncateMinuteStart(nowMillis) - (pointCount - 1) * ChronoUnit.MINUTES.getDuration().toMillis();
            }
            return truncateHourStart(nowMillis) - (pointCount - 1) * ChronoUnit.HOURS.getDuration().toMillis();
        }

        long nextBucketStart(long bucketStartAt) {
            if ("month".equals(bucketUnit)) {
                return shiftMonths(bucketStartAt, 1);
            }
            if ("day".equals(bucketUnit)) {
                return bucketStartAt + ChronoUnit.DAYS.getDuration().toMillis();
            }
            if ("hour".equals(bucketUnit)) {
                return bucketStartAt + ChronoUnit.HOURS.getDuration().toMillis();
            }
            if ("minute".equals(bucketUnit)) {
                return bucketStartAt + ChronoUnit.MINUTES.getDuration().toMillis();
            }
            return bucketStartAt + ChronoUnit.HOURS.getDuration().toMillis();
        }

        private long truncateMinuteStart(long nowMillis) {
            return Instant.ofEpochMilli(nowMillis)
                    .atZone(ZoneId.systemDefault())
                    .truncatedTo(ChronoUnit.MINUTES)
                    .toInstant()
                    .toEpochMilli();
        }

        private long truncateHourStart(long nowMillis) {
            return Instant.ofEpochMilli(nowMillis)
                    .atZone(ZoneId.systemDefault())
                    .truncatedTo(ChronoUnit.HOURS)
                    .toInstant()
                    .toEpochMilli();
        }

        private long truncateDayStart(long nowMillis) {
            return Instant.ofEpochMilli(nowMillis)
                    .atZone(ZoneId.systemDefault())
                    .truncatedTo(ChronoUnit.DAYS)
                    .toInstant()
                    .toEpochMilli();
        }

        private long truncateMonthStart(long nowMillis) {
            return Instant.ofEpochMilli(nowMillis)
                    .atZone(ZoneId.systemDefault())
                    .withDayOfMonth(1)
                    .truncatedTo(ChronoUnit.DAYS)
                    .toInstant()
                    .toEpochMilli();
        }

        private long shiftMonths(long millis, int months) {
            return Instant.ofEpochMilli(millis)
                    .atZone(ZoneId.systemDefault())
                    .plusMonths(months)
                    .withDayOfMonth(1)
                    .truncatedTo(ChronoUnit.DAYS)
                    .toInstant()
                    .toEpochMilli();
        }
    }
}
