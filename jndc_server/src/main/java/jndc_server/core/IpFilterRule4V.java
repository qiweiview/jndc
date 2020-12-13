package jndc_server.core;

import jndc.core.data_store_support.DSKey;
import jndc.core.data_store_support.DSTable;

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

    @Override
    public String toString() {
        return "IpFilterRule4V{" +
                "ip='" + ip + '\'' +
                ", type=" + type +
                '}';
    }

    /* -------getter setter------- */

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
