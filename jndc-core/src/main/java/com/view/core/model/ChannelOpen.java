package com.view.core.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * 通道打开
 */
@Data
@Slf4j
public class ChannelOpen implements Serializable {
    //磁盘固定生成
    private String ndcClientId;

    private String ndcServerId;


}
