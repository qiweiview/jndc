package com.view.jndc.manage.model.jndc_client.vo;

import lombok.Data;

@Data
public class JndcClientVO {

    /**
     * 自动重连
     */
    private Integer autoReconnect;

    /**
     * 客户端名称
     */
    private String clientName;

    /**
     * 客户端备注
     */
    private String clientRemark;

    /** 客户端状态 */
    private String clientStatus;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /** 伪装协议 */
    private String disguisedProtocol;

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
     * 重连间隔
     */
    private Integer reconnectInterval;

    /**
     * 重连次数限制
     */
    private Integer reconnectMaxTimes;

    /**
     * 服务主机
     */
    private String serverHost;

    /**
     * 服务端口
     */
    private Integer serverPort;

  /** 唯一id */
  private String uniqueId;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;
}
