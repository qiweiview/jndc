package cn.view.jndc.server_sv.web_support.model.vo;


import lombok.Data;

@Data
public class ChannelContextVO {

    private String id;

    private int supportServiceNum;

    private int channelClientPort;

    private String channelClientIp;

    private long lastHearBeatTimeStamp;


}
