package com.view.jndc.core.v2.model.json_object;

import lombok.Data;

@Data
public class DataTransmission extends JSONSerializable {

    //代理器编号
    private String proxyId;

    //调用源编号
    private String sourceId;

    //目标编号(冗余)
    private String descId;

    private byte[] data;
}
