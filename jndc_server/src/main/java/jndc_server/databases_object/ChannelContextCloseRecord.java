package jndc_server.databases_object;

import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import jndc_server.core.ChannelHandlerContextHolder;

@DSTable(name = "channel_context_record")
public class ChannelContextCloseRecord {

    @DSKey
    private String id;

    private String channelId;

    private String ip;

    private int port;

    private long timeStamp;

    public static ChannelContextCloseRecord of(ChannelHandlerContextHolder value) {
        ChannelContextCloseRecord channelContextCloseRecord = new ChannelContextCloseRecord();
        channelContextCloseRecord.setChannelId(value.getClientId());
        channelContextCloseRecord.setIp(value.getContextIp());
        channelContextCloseRecord.setPort(value.getContextPort());
        return channelContextCloseRecord;
    }


    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
