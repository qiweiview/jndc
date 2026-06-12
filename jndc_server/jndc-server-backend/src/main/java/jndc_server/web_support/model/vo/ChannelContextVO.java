package jndc_server.web_support.model.vo;


import lombok.Data;

@Data
public class ChannelContextVO {

    private String channelId;

    private String clientId;

    private int serviceCount;

    private int clientPort;

    private String clientIp;

    private long lastHeartbeat;

    private boolean connected;

    private int authMode;


}
