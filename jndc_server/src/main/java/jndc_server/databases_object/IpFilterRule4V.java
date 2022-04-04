package jndc_server.databases_object;

import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;
import lombok.Data;

@Data
@DSTable(name = "server_ip_filter_rule")
public class IpFilterRule4V {
    @DSKey
    private String id;

    private String ip;

    private int type; //0 white 1 black

    public void black(){
        this.type=1;
    }

    public void white(){
        this.type=0;
    }

    public boolean isBlack(){
        return type==1;
    }


}
