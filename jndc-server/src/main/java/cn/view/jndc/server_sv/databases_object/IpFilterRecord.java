package cn.view.jndc.server_sv.databases_object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jndc.core.data_store_support.DSFiled;
import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@DSTable(name = "ip_filter_record")
public class IpFilterRecord {

    @DSKey
    private String id;

    private String ip;

    @DSFiled(name = "v_count")
    @JsonProperty("vCount")
    private int vCount;

    @DSFiled(name = "time_stamp")
    private long timeStamp;

    @DSFiled(name = "record_type")
    @JsonProperty("recordType")
    private int recordType;


}
