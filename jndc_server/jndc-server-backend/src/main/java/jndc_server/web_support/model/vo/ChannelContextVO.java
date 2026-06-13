package jndc_server.web_support.model.vo;


import lombok.Data;

import java.util.List;

@Data
public class ChannelContextVO {

    private String channelId;

    private String clientId;

    private int serviceCount;

    private int clientPort;

    private String clientIp;

    private long lastHeartbeat;

    private boolean connected;

    private boolean online;

    private int authMode;

    private long lastSeenAt;

    private long lastOfflineAt;

    private String osName;

    private String osVersion;

    private String cpuModel;

    private int cpuLogicalCores;

    private List<String> gpuNames;

    private long memoryTotalBytes;

    private long diskTotalBytes;

    private long diskFreeBytes;

    private long clientToServerBytes;

    private long serverToClientBytes;

    private long clientToServerBandwidth;

    private long serverToClientBandwidth;

    private long trafficUpdatedAt;


}
