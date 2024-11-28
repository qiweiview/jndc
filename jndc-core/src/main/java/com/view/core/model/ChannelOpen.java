package com.view.core.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 通道打开
 */
@Data
@Slf4j
public class ChannelOpen extends TraceableEntity implements Serializable {
    //磁盘固定生成
    private String ndcClientId;

    private String ndcServerId;

    public static String ddl() {
        return "create table if not exists channel_open (id bigint primary key, ndc_client_id varchar(255), ndc_server_id varchar(255), create_date datetime, update_date datetime)";
    }

}
