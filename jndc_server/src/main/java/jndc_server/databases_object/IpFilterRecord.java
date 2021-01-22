package jndc_server.databases_object;

import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;

@DSTable(name = "ip_filter_record")
public class IpFilterRecord {

    @DSKey
    private String id;

    private String ip;

    private int vCount;

    private long timeStamp;

    private int recordType;


    public int getRecordType() {
        return recordType;
    }

    public void setRecordType(int recordType) {
        this.recordType = recordType;
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

    public int getvCount() {
        return vCount;
    }

    public void setvCount(int vCount) {
        this.vCount = vCount;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
