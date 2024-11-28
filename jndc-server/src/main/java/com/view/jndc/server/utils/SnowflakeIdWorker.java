package com.view.jndc.server.utils;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class SnowflakeIdWorker {

    private static final long TWEPOCH = 1682870400000L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private final long maxWorkerId = -1L ^ (-1L << WORKER_ID_BITS);
    private final long maxDatacenterId = -1L ^ (-1L << DATACENTER_ID_BITS);
    private final long maxSequence = -1L ^ (-1L << SEQUENCE_BITS);

    private final long workerIdShift = SEQUENCE_BITS;
    private final long datacenterIdShift = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;
    @Setter
    private long workerId;
    @Setter
    private long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private final ReentrantLock lock = new ReentrantLock();
    private volatile AtomicLong customTimestamp;

    public SnowflakeIdWorker(long workerId, long datacenterId) {
        this(workerId, datacenterId, null);
    }

    public SnowflakeIdWorker(long workerId, long datacenterId, Long customTimestamp) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("Worker ID can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("Datacenter ID can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        if (customTimestamp != null) {
            this.customTimestamp = new AtomicLong(customTimestamp);
        }
    }

    public SnowflakeIdWorker withCustomTimestamp(Long customTimestamp) {
        return new SnowflakeIdWorker(workerId, datacenterId, customTimestamp);
    }


    public SnowflakeIdWorker withCustomTimestamp(LocalDateTime localDateTime) {
        return withCustomTimestamp(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }


    public long nextId() {
        Long id;
        if (customTimestamp != null) {
            id = generateNextId(customTimestamp.get());
        } else {
            id = generateNextId(System.currentTimeMillis());
        }
        return id;
    }

    private long generateNextId(long timestamp) {
        lock.lock();
        try {
            if (timestamp < lastTimestamp) {
                log.error("时间回拨{},{}", timestamp, lastTimestamp);
                throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
            }

            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & maxSequence;
                if (sequence == 0) {
                    timestamp = waitForNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }

            lastTimestamp = timestamp;

            return ((timestamp - TWEPOCH) << TIMESTAMP_LEFT_SHIFT) |
                    (datacenterId << datacenterIdShift) |
                    (workerId << workerIdShift) |
                    sequence;
        } finally {
            lock.unlock();
        }
    }

    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    private long waitForNextMillis(long lastTimestamp) {
        if (customTimestamp != null) {
            return customTimestamp.incrementAndGet();
        } else {
            long timestamp = getCurrentTimestamp();
            while (timestamp <= lastTimestamp) {
                timestamp = getCurrentTimestamp();
            }
            return timestamp;
        }
    }

    /**
     * 解析出时间戳
     *
     * @param id
     * @return
     */
    public static long extractTimestamp(Long id) {
        if (id == null) {
            throw new RuntimeException("id不能为空");
        }
        return (id >> TIMESTAMP_LEFT_SHIFT) + TWEPOCH;
    }

    private static LocalDate extractLocalDate(Long id) {
        return Instant.ofEpochMilli(extractTimestamp(id)).atZone(ZoneId.systemDefault()).toLocalDate();
    }


    public static LocalDateTime extractLocalDateTime(Long id) {
        return Instant.ofEpochMilli(extractTimestamp(id)).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }


    public static int extractYear(Long id) {
        return extractLocalDate(id).getYear();
    }
}
