package jndc_server.databases_object;

import jndc.core.data_store_support.DSFiled;
import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import lombok.Data;

@Data
@DSTable(name = "ip_filter_record")
public class IpFilterRecord {

    @DSKey
    private String id;

    private String ip;

    @DSFiled(name = "v_count")
    private int vCount;

    @DSFiled(name = "time_stamp")
    private long timeStamp;

    @DSFiled(name = "record_type")
    private int recordType;


}
