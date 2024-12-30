package com.view.jndc.manage.model.jndc_server_app.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class JndcServerAppVO {

  /** 监听域名 */
  private String bindHost;

  /** 监听端口 */
  private String bindPort;

  /** 监听状态 */
  private String bindStatus;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** id */
  private Long id;

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

  /** jndc服务id */
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

  /** 来源客户端 */
  private String sourceClientId;

  /** 来源服务 */
  private String sourceServiceId;
}
