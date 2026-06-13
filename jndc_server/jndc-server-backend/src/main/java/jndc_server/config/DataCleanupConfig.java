package jndc_server.config;

import lombok.Data;

@Data
public class DataCleanupConfig {

    /**
     * 是否开启运行时数据清理
     */
    private boolean enabled = true;

    /**
     * 清理任务执行周期，单位小时
     */
    private long runIntervalHours = 24L;

    /**
     * 断连记录保留天数
     */
    private long channelRecordRetentionDays = 30L;

    /**
     * IP过滤命中记录保留天数
     */
    private long ipFilterRecordRetentionDays = 30L;

    /**
     * 分钟级流量趋势保留天数
     */
    private long trafficTrendMinuteRetentionDays = 3L;

    /**
     * 小时级流量趋势保留天数
     */
    private long trafficTrendHourRetentionDays = 14L;

    /**
     * 天级流量趋势保留天数
     */
    private long trafficTrendDayRetentionDays = 90L;

    /**
     * 月级流量趋势保留天数
     */
    private long trafficTrendMonthRetentionDays = 1095L;

    /**
     * 清理后是否执行VACUUM回收SQLite磁盘空间
     */
    private boolean vacuumAfterCleanup = true;
}
