package cn.view.jndc.server_sv.core.filter.ip_checker;

import lombok.Data;

@Data
public class IpRecord {

    public static final int RELEASE_STATE = 0;
    public static final int BLOCK_STATE = 1;

    private String ip;

    private int tag;//0 release 1 block


    public boolean isRelease() {
        return tag == 0;
    }


    public IpRecord(String ip, int tag) {
        this.ip = ip;
        this.tag = tag;
    }


}
