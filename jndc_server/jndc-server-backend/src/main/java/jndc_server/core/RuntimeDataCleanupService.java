package jndc_server.core;

import jndc.core.data_store_support.DataStoreAbstract;
import jndc_server.config.DataCleanupConfig;
import jndc_server.config.JNDCServerConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 运行时数据清理服务
 */
@Slf4j
public class RuntimeDataCleanupService {

    private static final String BUCKET_TYPE_MINUTE = "MINUTE";

    private static final String BUCKET_TYPE_HOUR = "HOUR";

    private static final String BUCKET_TYPE_DAY = "DAY";

    private static final String BUCKET_TYPE_MONTH = "MONTH";

    private final JNDCServerConfig serverConfig;

    private final DataStoreAbstract dataStoreAbstract;

    public RuntimeDataCleanupService(JNDCServerConfig serverConfig, DataStoreAbstract dataStoreAbstract) {
        this.serverConfig = serverConfig;
        this.dataStoreAbstract = dataStoreAbstract;
    }

    public long getRunIntervalHours() {
        DataCleanupConfig cleanupConfig = serverConfig.getCleanupConfig();
        if (cleanupConfig == null || cleanupConfig.getRunIntervalHours() <= 0) {
            return 24L;
        }
        return cleanupConfig.getRunIntervalHours();
    }

    public CleanupResult cleanupExpiredData() {
        return cleanupExpiredData(System.currentTimeMillis());
    }

    CleanupResult cleanupExpiredData(long nowMillis) {
        DataCleanupConfig cleanupConfig = serverConfig.getCleanupConfig();
        if (cleanupConfig == null || !cleanupConfig.isEnabled()) {
            return CleanupResult.disabled();
        }

        long startAt = System.currentTimeMillis();
        CleanupResult cleanupResult = new CleanupResult();
        cleanupResult.setEnabled(true);
        cleanupResult.setChannelContextRows(deleteExpiredRows(
                "delete from channel_context_record where time_stamp < ?",
                cleanupConfig.getChannelRecordRetentionDays(),
                nowMillis
        ));
        cleanupResult.setIpFilterRows(deleteExpiredRows(
                "delete from ip_filter_record where time_stamp < ?",
                cleanupConfig.getIpFilterRecordRetentionDays(),
                nowMillis
        ));
        cleanupResult.setTrendMinuteRows(deleteExpiredTrendRows(
                BUCKET_TYPE_MINUTE,
                cleanupConfig.getTrafficTrendMinuteRetentionDays(),
                nowMillis
        ));
        cleanupResult.setTrendHourRows(deleteExpiredTrendRows(
                BUCKET_TYPE_HOUR,
                cleanupConfig.getTrafficTrendHourRetentionDays(),
                nowMillis
        ));
        cleanupResult.setTrendDayRows(deleteExpiredTrendRows(
                BUCKET_TYPE_DAY,
                cleanupConfig.getTrafficTrendDayRetentionDays(),
                nowMillis
        ));
        cleanupResult.setTrendMonthRows(deleteExpiredTrendRows(
                BUCKET_TYPE_MONTH,
                cleanupConfig.getTrafficTrendMonthRetentionDays(),
                nowMillis
        ));

        if (cleanupConfig.isVacuumAfterCleanup() && cleanupResult.totalDeletedRows() > 0) {
            dataStoreAbstract.execute("VACUUM", null);
            cleanupResult.setVacuumExecuted(true);
        }
        cleanupResult.setDurationMillis(System.currentTimeMillis() - startAt);
        log.info(
                "runtime data cleanup finished, deletedRows={}, channel={}, ipFilter={}, trendMinute={}, trendHour={}, trendDay={}, trendMonth={}, vacuum={}, cost={}ms",
                cleanupResult.totalDeletedRows(),
                cleanupResult.getChannelContextRows(),
                cleanupResult.getIpFilterRows(),
                cleanupResult.getTrendMinuteRows(),
                cleanupResult.getTrendHourRows(),
                cleanupResult.getTrendDayRows(),
                cleanupResult.getTrendMonthRows(),
                cleanupResult.isVacuumExecuted(),
                cleanupResult.getDurationMillis()
        );
        return cleanupResult;
    }

    private int deleteExpiredTrendRows(String bucketType, long retentionDays, long nowMillis) {
        if (retentionDays <= 0) {
            return 0;
        }
        long cutoff = nowMillis - TimeUnit.DAYS.toMillis(retentionDays);
        return dataStoreAbstract.executeUpdate(
                "delete from client_traffic_trend_record where bucket_type = ? and bucket_start_at < ?",
                new Object[]{bucketType, cutoff}
        );
    }

    private int deleteExpiredRows(String sql, long retentionDays, long nowMillis) {
        if (retentionDays <= 0) {
            return 0;
        }
        long cutoff = nowMillis - TimeUnit.DAYS.toMillis(retentionDays);
        return dataStoreAbstract.executeUpdate(sql, new Object[]{cutoff});
    }

    @Data
    public static class CleanupResult {
        private boolean enabled;
        private boolean vacuumExecuted;
        private long durationMillis;
        private int channelContextRows;
        private int ipFilterRows;
        private int trendMinuteRows;
        private int trendHourRows;
        private int trendDayRows;
        private int trendMonthRows;

        static CleanupResult disabled() {
            CleanupResult cleanupResult = new CleanupResult();
            cleanupResult.setEnabled(false);
            return cleanupResult;
        }

        int totalDeletedRows() {
            return channelContextRows
                    + ipFilterRows
                    + trendMinuteRows
                    + trendHourRows
                    + trendDayRows
                    + trendMonthRows;
        }
    }
}
