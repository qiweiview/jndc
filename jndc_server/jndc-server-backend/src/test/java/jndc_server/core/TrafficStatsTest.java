package jndc_server.core;

import jndc.core.NDCMessageProtocol;
import jndc.core.UniqueBeanManage;
import jndc.core.data_store_support.DBWrapper;
import jndc.core.data_store_support.DataStoreAbstract;
import jndc.core.data_store_support.SQLiteDataStore;
import jndc_server.databases_object.ClientAuthRecord;
import jndc_server.databases_object.ClientTrafficTrendRecord;
import jndc_server.web_support.mapping.ServerManageMapping;
import jndc_server.web_support.model.vo.ChannelContextVO;
import jndc_server.web_support.model.vo.ChannelTrafficTrendVO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TrafficStatsTest {

    @Before
    public void setUp() throws Exception {
        clearStaticMap(UniqueBeanManage.class, "map");
    }

    @After
    public void tearDown() throws Exception {
        clearStaticMap(UniqueBeanManage.class, "map");
    }

    @Test
    public void shouldTrackBandwidthWithinRollingWindow() throws Exception {
        registerDataStore();
        TCPDataFlowAnalysisCenter analysisCenter = registerAnalysisCenter();

        analysisCenter.analyse("client-a", messageOfSize(100), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, 0L);
        analysisCenter.analyse("client-a", messageOfSize(200), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, 1000L);
        analysisCenter.analyse("client-a", messageOfSize(300), TCPDataFlowAnalysisCenter.DIRECTION_SERVER_TO_CLIENT, 2000L);

        TCPDataFlowAnalysisCenter.TrafficSnapshot snapshot = analysisCenter.getTrafficSnapshot("client-a", true, 4000L);
        assertEquals(60L, snapshot.getClientToServerBandwidth());
        assertEquals(60L, snapshot.getServerToClientBandwidth());
        assertEquals(0L, snapshot.getPendingClientToServerBytes());
        assertEquals(0L, snapshot.getPendingServerToClientBytes());

        analysisCenter.analyse("client-a", messageOfSize(400), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, 6000L);
        TCPDataFlowAnalysisCenter.TrafficSnapshot rolledSnapshot = analysisCenter.getTrafficSnapshot("client-a", true, 6000L);
        assertEquals(80L, rolledSnapshot.getClientToServerBandwidth());
        assertEquals(60L, rolledSnapshot.getServerToClientBandwidth());

        TCPDataFlowAnalysisCenter.TrafficSnapshot offlineSnapshot = analysisCenter.getTrafficSnapshot("client-a", false, 6000L);
        assertEquals(0L, offlineSnapshot.getClientToServerBandwidth());
        assertEquals(0L, offlineSnapshot.getServerToClientBandwidth());
    }

    @Test
    public void shouldExposePersistedAndPendingTrafficInChannelTable() throws Exception {
        registerDataStore();
        NDCServerConfigCenter configCenter = new NDCServerConfigCenter();
        UniqueBeanManage.registerBean(configCenter);
        TCPDataFlowAnalysisCenter analysisCenter = registerAnalysisCenter();

        DBWrapper<ClientAuthRecord> dbWrapper = DBWrapper.getDBWrapper(ClientAuthRecord.class);

        ClientAuthRecord offlineRecord = new ClientAuthRecord();
        offlineRecord.setClientId("client-offline");
        offlineRecord.setClientAuthKey("offline-key");
        offlineRecord.setClientToServerBytes(33L);
        offlineRecord.setServerToClientBytes(44L);
        dbWrapper.insert(offlineRecord);

        ClientAuthRecord onlineRecord = new ClientAuthRecord();
        onlineRecord.setClientId("client-online");
        onlineRecord.setClientAuthKey("online-key");
        onlineRecord.setClientToServerBytes(100L);
        onlineRecord.setServerToClientBytes(200L);
        onlineRecord.setLastClientIp("10.0.0.8");
        onlineRecord.setLastClientPort(1081);
        onlineRecord.setLastSeenAt(12345L);
        dbWrapper.insert(onlineRecord);

        ChannelHandlerContextHolder onlineHolder = new ChannelHandlerContextHolder("client-online");
        onlineHolder.setAuthMode(1);
        onlineHolder.setContextIp("10.0.0.8");
        onlineHolder.setContextPort(1081);
        onlineHolder.setLastHearBeatTimeStamp(99999L);
        onlineHolder.setTcpServiceDescriptions(new CopyOnWriteArrayList<ServerServiceDescription>());
        onlineHolder.getTcpServiceDescriptions().add(new ServerServiceDescription());
        setOnlineHolder(configCenter, onlineHolder);

        long nowMillis = System.currentTimeMillis();
        analysisCenter.analyse("client-online", messageOfSize(50), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, nowMillis);
        analysisCenter.analyse("client-online", messageOfSize(70), TCPDataFlowAnalysisCenter.DIRECTION_SERVER_TO_CLIENT, nowMillis + 1);

        ServerManageMapping mapping = new ServerManageMapping();
        List<ChannelContextVO> table = mapping.getServerChannelTable(null);

        ChannelContextVO offline = findByClientId(table, "client-offline");
        assertFalse(offline.isOnline());
        assertEquals(33L, offline.getClientToServerBytes());
        assertEquals(44L, offline.getServerToClientBytes());
        assertEquals(0L, offline.getClientToServerBandwidth());
        assertEquals(0L, offline.getServerToClientBandwidth());

        ChannelContextVO online = findByClientId(table, "client-online");
        assertTrue(online.isOnline());
        assertEquals(150L, online.getClientToServerBytes());
        assertEquals(270L, online.getServerToClientBytes());
        assertEquals(10L, online.getClientToServerBandwidth());
        assertEquals(14L, online.getServerToClientBandwidth());
        assertEquals(nowMillis + 1, online.getTrafficUpdatedAt());
    }

    @Test
    public void shouldPersistTrafficIntoTrendBuckets() throws Exception {
        registerDataStore();
        TCPDataFlowAnalysisCenter analysisCenter = registerAnalysisCenter();

        long t1 = timeOf(2026, 6, 10, 10, 5);
        long t2 = timeOf(2026, 6, 10, 11, 15);
        long t3 = timeOf(2026, 6, 12, 9, 0);
        long t4 = timeOf(2026, 7, 2, 8, 0);

        analysisCenter.analyse("client-a", messageOfSize(100), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, t1);
        analysisCenter.analyse("client-a", messageOfSize(40), TCPDataFlowAnalysisCenter.DIRECTION_SERVER_TO_CLIENT, t1 + 1);
        analysisCenter.analyse("client-a", messageOfSize(200), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, t2);
        analysisCenter.analyse("client-a", messageOfSize(80), TCPDataFlowAnalysisCenter.DIRECTION_SERVER_TO_CLIENT, t3);
        analysisCenter.analyse("client-a", messageOfSize(160), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, t4);

        DBWrapper<ClientTrafficTrendRecord> dbWrapper = DBWrapper.getDBWrapper(ClientTrafficTrendRecord.class);

        ClientTrafficTrendRecord hour10 = dbWrapper.customQuerySingle(
                "select * from client_traffic_trend_record where id=?",
                "client-a_HOUR_" + truncateToHour(t1)
        );
        assertEquals(100L, hour10.getClientToServerBytes().longValue());
        assertEquals(40L, hour10.getServerToClientBytes().longValue());

        ClientTrafficTrendRecord minute1005 = dbWrapper.customQuerySingle(
                "select * from client_traffic_trend_record where id=?",
                "client-a_MINUTE_" + truncateToMinute(t1)
        );
        assertEquals(100L, minute1005.getClientToServerBytes().longValue());
        assertEquals(40L, minute1005.getServerToClientBytes().longValue());

        ClientTrafficTrendRecord day0610 = dbWrapper.customQuerySingle(
                "select * from client_traffic_trend_record where id=?",
                "client-a_DAY_" + truncateToDay(t1)
        );
        assertEquals(300L, day0610.getClientToServerBytes().longValue());
        assertEquals(40L, day0610.getServerToClientBytes().longValue());

        ClientTrafficTrendRecord month06 = dbWrapper.customQuerySingle(
                "select * from client_traffic_trend_record where id=?",
                "client-a_MONTH_" + truncateToMonth(t1)
        );
        assertEquals(300L, month06.getClientToServerBytes().longValue());
        assertEquals(120L, month06.getServerToClientBytes().longValue());

        ClientTrafficTrendRecord month07 = dbWrapper.customQuerySingle(
                "select * from client_traffic_trend_record where id=?",
                "client-a_MONTH_" + truncateToMonth(t4)
        );
        assertEquals(160L, month07.getClientToServerBytes().longValue());
        assertEquals(0L, month07.getServerToClientBytes().longValue());
    }

    @Test
    public void shouldReturnZeroFilledTrafficTrendSeries() throws Exception {
        registerDataStore();
        TCPDataFlowAnalysisCenter analysisCenter = registerAnalysisCenter();

        long now = timeOf(2026, 7, 15, 10, 10);
        long minuteBucket = truncateToMinute(now);
        long hourBucket = truncateToHour(now);
        long yesterdayBucket = truncateToDay(now - ChronoUnit.DAYS.getDuration().toMillis());
        long monthBucket = truncateToMonth(now);

        analysisCenter.analyse("client-a", messageOfSize(120), TCPDataFlowAnalysisCenter.DIRECTION_SERVER_TO_CLIENT, monthBucket + 24 * 60 * 60 * 1000);
        analysisCenter.analyse("client-a", messageOfSize(60), TCPDataFlowAnalysisCenter.DIRECTION_SERVER_TO_CLIENT, yesterdayBucket + 2 * 60 * 60 * 1000);
        analysisCenter.analyse("client-a", messageOfSize(90), TCPDataFlowAnalysisCenter.DIRECTION_CLIENT_TO_SERVER, hourBucket + 5 * 60 * 1000);

        ChannelTrafficTrendVO trend1hour = analysisCenter.getTrafficTrend("client-a", "1hour", now);
        assertEquals("1hour", trend1hour.getRange());
        assertEquals("minute", trend1hour.getBucketUnit());
        assertEquals(60, trend1hour.getPoints().size());
        assertEquals(minuteBucket, trend1hour.getPoints().get(59).getTimestamp());
        assertEquals(0L, trend1hour.getPoints().get(59).getTotalBytes());
        assertEquals(90L, findPointByTimestamp(trend1hour, hourBucket + 5 * 60 * 1000).getClientToServerBytes());
        assertEquals(90L, findPointByTimestamp(trend1hour, hourBucket + 5 * 60 * 1000).getTotalBytes());

        ChannelTrafficTrendVO trend24hour = analysisCenter.getTrafficTrend("client-a", "24hour", now);
        assertEquals("24hour", trend24hour.getRange());
        assertEquals("hour", trend24hour.getBucketUnit());
        assertEquals(24, trend24hour.getPoints().size());
        assertEquals(hourBucket, trend24hour.getPoints().get(23).getTimestamp());
        assertEquals(90L, trend24hour.getPoints().get(23).getClientToServerBytes());
        assertEquals(0L, trend24hour.getPoints().get(23).getServerToClientBytes());

        ChannelTrafficTrendVO trend7day = analysisCenter.getTrafficTrend("client-a", "7day", now);
        assertEquals(7, trend7day.getPoints().size());
        assertEquals(truncateToDay(now), trend7day.getPoints().get(6).getTimestamp());
        assertEquals(yesterdayBucket, trend7day.getPoints().get(5).getTimestamp());
        assertEquals(60L, trend7day.getPoints().get(5).getServerToClientBytes());

        ChannelTrafficTrendVO trend1month = analysisCenter.getTrafficTrend("client-a", "1month", now);
        assertEquals(30, trend1month.getPoints().size());
        assertEquals(truncateToDay(now), trend1month.getPoints().get(29).getTimestamp());
        assertEquals(0L, trend1month.getPoints().get(29).getServerToClientBytes());
        assertEquals(90L, trend1month.getPoints().get(29).getTotalBytes());
        assertEquals(120L, findPointByTimestamp(trend1month, monthBucket + 24 * 60 * 60 * 1000).getServerToClientBytes());
        assertEquals(120L, findPointByTimestamp(trend1month, monthBucket + 24 * 60 * 60 * 1000).getTotalBytes());

        ChannelTrafficTrendVO trend1year = analysisCenter.getTrafficTrend("client-a", "1year", now);
        assertEquals(12, trend1year.getPoints().size());
        assertEquals("month", trend1year.getBucketUnit());
        assertEquals(monthBucket, trend1year.getPoints().get(11).getTimestamp());
        assertEquals(90L, trend1year.getPoints().get(11).getClientToServerBytes());
        assertEquals(180L, trend1year.getPoints().get(11).getServerToClientBytes());

        ChannelTrafficTrendVO emptyTrend = analysisCenter.getTrafficTrend("missing-client", "24hour", now);
        assertEquals(24, emptyTrend.getPoints().size());
        assertEquals(0L, emptyTrend.getPoints().get(0).getTotalBytes());
        assertEquals(0L, emptyTrend.getPoints().get(23).getTotalBytes());
    }

    private TCPDataFlowAnalysisCenter registerAnalysisCenter() {
        TCPDataFlowAnalysisCenter analysisCenter = new TCPDataFlowAnalysisCenter(new ImmediateAsynchronousEventCenter());
        UniqueBeanManage.registerBean(analysisCenter);
        return analysisCenter;
    }

    private void registerDataStore() throws Exception {
        File runtimeDir = Files.createTempDirectory("jndc-traffic-test").toFile();
        runtimeDir.deleteOnExit();
        SQLiteDataStore dataStore = new SQLiteDataStore(runtimeDir.getAbsolutePath());
        dataStore.init();
        UniqueBeanManage.registerBean(DataStoreAbstract.class, dataStore);
    }

    private void setOnlineHolder(NDCServerConfigCenter configCenter, ChannelHandlerContextHolder holder) throws Exception {
        Field field = NDCServerConfigCenter.class.getDeclaredField("channelHandlerContextHolderMap");
        field.setAccessible(true);
        Map<String, ChannelHandlerContextHolder> holderMap = new ConcurrentHashMap<>();
        holderMap.put(holder.getClientId(), holder);
        field.set(configCenter, holderMap);
    }

    private ChannelContextVO findByClientId(List<ChannelContextVO> table, String clientId) {
        for (ChannelContextVO channelContextVO : table) {
            if (clientId.equals(channelContextVO.getClientId())) {
                return channelContextVO;
            }
        }
        throw new AssertionError("client not found: " + clientId);
    }

    private NDCMessageProtocol messageOfSize(int size) {
        NDCMessageProtocol protocol = new NDCMessageProtocol();
        protocol.setType(NDCMessageProtocol.TCP_DATA);
        protocol.setData(new byte[size]);
        return protocol;
    }

    private void clearStaticMap(Class<?> owner, String fieldName) throws Exception {
        Field field = owner.getDeclaredField(fieldName);
        field.setAccessible(true);
        Map<?, ?> map = (Map<?, ?>) field.get(null);
        map.clear();
    }

    private long timeOf(int year, int month, int day, int hour, int minute) {
        return ZonedDateTime.of(year, month, day, hour, minute, 0, 0, ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    private long truncateToHour(long millis) {
        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .truncatedTo(ChronoUnit.HOURS)
                .toInstant()
                .toEpochMilli();
    }

    private long truncateToMinute(long millis) {
        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .truncatedTo(ChronoUnit.MINUTES)
                .toInstant()
                .toEpochMilli();
    }

    private long truncateToDay(long millis) {
        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .truncatedTo(ChronoUnit.DAYS)
                .toInstant()
                .toEpochMilli();
    }

    private long truncateToMonth(long millis) {
        return Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .withDayOfMonth(1)
                .truncatedTo(ChronoUnit.DAYS)
                .toInstant()
                .toEpochMilli();
    }

    private jndc_server.web_support.model.vo.ChannelTrafficTrendPointVO findPointByTimestamp(ChannelTrafficTrendVO trend, long timestamp) {
        for (jndc_server.web_support.model.vo.ChannelTrafficTrendPointVO point : trend.getPoints()) {
            if (point.getTimestamp() == timestamp) {
                return point;
            }
        }
        throw new AssertionError("point not found: " + timestamp);
    }

    private static class ImmediateAsynchronousEventCenter extends AsynchronousEventCenter {
        @Override
        public void dataAnalyseJob(Runnable runnable) {
            runnable.run();
        }
    }
}
