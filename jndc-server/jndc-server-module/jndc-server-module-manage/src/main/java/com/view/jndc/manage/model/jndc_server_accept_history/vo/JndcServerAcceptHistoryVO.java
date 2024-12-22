package com.view.jndc.manage.model.jndc_server_accept_history.vo;

import lombok.Data;

@Data
public class JndcServerAcceptHistoryVO {

    /**
     * 客户端id
     */
    private Long clientId;

    /**
     * 连接时间
     */
    private java.time.LocalDateTime connectTime;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * id
     */
    private Long id;

    /**
     * 字符id（处理浏览器long精度丢失问题）
     */
    private String idString;

    public void setId(Long id) {
        this.id = id;
        if (id != null && idString == null) {
            this.idString = id.toString();
        }
    }

    public void setIdString(String idString) {
        this.idString = idString;
        if (idString != null) {
            this.id = Long.parseLong(idString);
        }
    }

    /**
     * 中断时间
     */
    private java.time.LocalDateTime interruptTime;

    /**
     * 服务id
     */
    private Long serverId;

    private String serverIdString;


    public void setServerId(Long serverId) {
        this.serverId = serverId;
        if (serverId != null && serverIdString == null) {
            this.serverIdString = serverId.toString();
        }
    }

    public void setServerIdString(String serverIdString) {
        this.serverIdString = serverIdString;
        if (serverIdString != null) {
            this.serverId = Long.parseLong(serverIdString);
        }
    }

    /**
     * 来源ip
     */
    private String sourceIp;

    /**
     * 来源端口
     */
    private Integer sourcePort;

    /**
     * 修改时间
     */
    private java.time.LocalDateTime updateTime;
}
