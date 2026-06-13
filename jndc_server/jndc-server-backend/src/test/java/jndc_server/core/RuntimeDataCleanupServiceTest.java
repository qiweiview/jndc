package jndc_server.core;

import jndc.core.data_store_support.DataStoreAbstract;
import jndc.core.data_store_support.SQLiteDataStore;
import jndc_server.config.DataCleanupConfig;
import jndc_server.config.JNDCServerConfig;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RuntimeDataCleanupServiceTest {

    @Test
    public void shouldDeleteExpiredRuntimeDataAndVacuum() throws Exception {
        DataStoreAbstract dataStoreAbstract = createDataStore();
        long nowMillis = 10L * 24 * 60 * 60 * 1000;

        insertChannelRecord(dataStoreAbstract, "channel-old", nowMillis - dayMillis(5));
        insertChannelRecord(dataStoreAbstract, "channel-new", nowMillis - dayMillis(1));

        insertIpFilterRecord(dataStoreAbstract, "ip-old", nowMillis - dayMillis(6), 0);
        insertIpFilterRecord(dataStoreAbstract, "ip-new", nowMillis - dayMillis(1), 1);

        insertTrendRecord(dataStoreAbstract, "trend-minute-old", "MINUTE", nowMillis - dayMillis(5));
        insertTrendRecord(dataStoreAbstract, "trend-minute-new", "MINUTE", nowMillis - dayMillis(1));
        insertTrendRecord(dataStoreAbstract, "trend-hour-old", "HOUR", nowMillis - dayMillis(10));
        insertTrendRecord(dataStoreAbstract, "trend-hour-new", "HOUR", nowMillis - dayMillis(1));
        insertTrendRecord(dataStoreAbstract, "trend-day-old", "DAY", nowMillis - dayMillis(50));
        insertTrendRecord(dataStoreAbstract, "trend-day-new", "DAY", nowMillis - dayMillis(5));
        insertTrendRecord(dataStoreAbstract, "trend-month-old", "MONTH", nowMillis - dayMillis(500));
        insertTrendRecord(dataStoreAbstract, "trend-month-new", "MONTH", nowMillis - dayMillis(50));

        RuntimeDataCleanupService cleanupService = new RuntimeDataCleanupService(serverConfig(), dataStoreAbstract);
        RuntimeDataCleanupService.CleanupResult cleanupResult = cleanupService.cleanupExpiredData(nowMillis);

        assertTrue(cleanupResult.isEnabled());
        assertTrue(cleanupResult.isVacuumExecuted());
        assertEquals(1, cleanupResult.getChannelContextRows());
        assertEquals(1, cleanupResult.getIpFilterRows());
        assertEquals(1, cleanupResult.getTrendMinuteRows());
        assertEquals(1, cleanupResult.getTrendHourRows());
        assertEquals(1, cleanupResult.getTrendDayRows());
        assertEquals(1, cleanupResult.getTrendMonthRows());

        assertEquals(1L, count(dataStoreAbstract, "select count(*) as c from channel_context_record"));
        assertEquals(1L, count(dataStoreAbstract, "select count(*) as c from ip_filter_record"));
        assertEquals(1L, count(dataStoreAbstract, "select count(*) as c from client_traffic_trend_record where bucket_type='MINUTE'"));
        assertEquals(1L, count(dataStoreAbstract, "select count(*) as c from client_traffic_trend_record where bucket_type='HOUR'"));
        assertEquals(1L, count(dataStoreAbstract, "select count(*) as c from client_traffic_trend_record where bucket_type='DAY'"));
        assertEquals(1L, count(dataStoreAbstract, "select count(*) as c from client_traffic_trend_record where bucket_type='MONTH'"));
    }

    @Test
    public void shouldSkipCleanupWhenDisabled() throws Exception {
        DataStoreAbstract dataStoreAbstract = createDataStore();
        long nowMillis = 10L * 24 * 60 * 60 * 1000;
        insertChannelRecord(dataStoreAbstract, "channel-old", nowMillis - dayMillis(30));

        JNDCServerConfig serverConfig = serverConfig();
        serverConfig.getCleanupConfig().setEnabled(false);
        RuntimeDataCleanupService cleanupService = new RuntimeDataCleanupService(serverConfig, dataStoreAbstract);

        RuntimeDataCleanupService.CleanupResult cleanupResult = cleanupService.cleanupExpiredData(nowMillis);

        assertFalse(cleanupResult.isEnabled());
        assertEquals(1L, count(dataStoreAbstract, "select count(*) as c from channel_context_record"));
    }

    private JNDCServerConfig serverConfig() {
        JNDCServerConfig serverConfig = new JNDCServerConfig();
        DataCleanupConfig cleanupConfig = new DataCleanupConfig();
        cleanupConfig.setChannelRecordRetentionDays(2L);
        cleanupConfig.setIpFilterRecordRetentionDays(2L);
        cleanupConfig.setTrafficTrendMinuteRetentionDays(2L);
        cleanupConfig.setTrafficTrendHourRetentionDays(7L);
        cleanupConfig.setTrafficTrendDayRetentionDays(30L);
        cleanupConfig.setTrafficTrendMonthRetentionDays(365L);
        cleanupConfig.setVacuumAfterCleanup(true);
        serverConfig.setCleanupConfig(cleanupConfig);
        return serverConfig;
    }

    private DataStoreAbstract createDataStore() throws Exception {
        File runtimeDir = Files.createTempDirectory("jndc-cleanup-test").toFile();
        runtimeDir.deleteOnExit();
        SQLiteDataStore dataStore = new SQLiteDataStore(runtimeDir.getAbsolutePath());
        dataStore.init();
        return dataStore;
    }

    private void insertChannelRecord(DataStoreAbstract dataStoreAbstract, String id, long timeStamp) {
        dataStoreAbstract.execute(
                "insert into channel_context_record(id, client_id, ip, channel_id, port, time_stamp, disconnect_reason) values(?,?,?,?,?,?,?)",
                new Object[]{id, "client-a", "127.0.0.1", "channel-a", 1081, timeStamp, "TEST"}
        );
    }

    private void insertIpFilterRecord(DataStoreAbstract dataStoreAbstract, String id, long timeStamp, int recordType) {
        dataStoreAbstract.execute(
                "insert into ip_filter_record(id, ip, v_count, time_stamp, record_type) values(?,?,?,?,?)",
                new Object[]{id, "127.0.0.1", 1, timeStamp, recordType}
        );
    }

    private void insertTrendRecord(DataStoreAbstract dataStoreAbstract, String id, String bucketType, long bucketStartAt) {
        dataStoreAbstract.execute(
                "insert into client_traffic_trend_record(id, client_id, bucket_type, bucket_start_at, client_to_server_bytes, server_to_client_bytes, updated_at) values(?,?,?,?,?,?,?)",
                new Object[]{id, "client-a", bucketType, bucketStartAt, 1L, 2L, bucketStartAt}
        );
    }

    private long count(DataStoreAbstract dataStoreAbstract, String sql) {
        List<Map> rows = dataStoreAbstract.executeQuery(sql, null);
        Number count = (Number) rows.get(0).get("c");
        return count.longValue();
    }

    private long dayMillis(long day) {
        return day * 24L * 60L * 60L * 1000L;
    }
}
