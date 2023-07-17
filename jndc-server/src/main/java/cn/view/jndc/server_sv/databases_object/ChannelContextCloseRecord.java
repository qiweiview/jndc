package cn.view.jndc.server_sv.databases_object;

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


    @DSFiled(name = "channel_id")
    private String channelId;

    private String ip;

    private int port;

    @DSFiled(name = "time_stamp")
    private long timeStamp;

    public static ChannelContextCloseRecord of(ChannelHandlerContextHolder value) {
        ChannelContextCloseRecord channelContextCloseRecord = new ChannelContextCloseRecord();
        channelContextCloseRecord.setChannelId(value.getClientId());
        channelContextCloseRecord.setIp(value.getContextIp());
        channelContextCloseRecord.setPort(value.getContextPort());
        return channelContextCloseRecord;
    }


}
