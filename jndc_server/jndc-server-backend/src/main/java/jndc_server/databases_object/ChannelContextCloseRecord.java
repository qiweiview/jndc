package jndc_server.databases_object;

import jndc.core.data_store_support.DSFiled;
import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import jndc_server.core.ChannelHandlerContextHolder;
import lombok.Data;

@Data
@DSTable(name = "channel_context_record")
public class ChannelContextCloseRecord {

    @DSKey
    private String id;

    @DSFiled(name = "client_id")
    private String clientId;

    @DSFiled(name = "channel_id")
    private String channelId;

    private String ip;

    private int port;

    @DSFiled(name = "time_stamp")
    private long timeStamp;

    @DSFiled(name = "disconnect_reason")
    private String disconnectReason;

    public static ChannelContextCloseRecord of(ChannelHandlerContextHolder value) {
        ChannelContextCloseRecord channelContextCloseRecord = new ChannelContextCloseRecord();
        channelContextCloseRecord.setClientId(value.getClientId());
        channelContextCloseRecord.setChannelId(value.getClientId());
        channelContextCloseRecord.setIp(value.getContextIp());
        channelContextCloseRecord.setPort(value.getContextPort());
        channelContextCloseRecord.setDisconnectReason(value.getDisconnectReason());
        return channelContextCloseRecord;
    }


}
