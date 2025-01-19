package com.view.jndc.manage.model.jndc_server_service.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class JndcServerServiceVO {

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

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;

  /** 所属客户端id */
  private Long clientId;

  private String clientIdString;

  public void setClientId(Long clientId) {
    this.clientId = clientId;
    if (clientId != null && clientIdString == null) {
      this.clientIdString = clientId.toString();
    }
  }

  public void setClientIdString(String clientIdString) {
    this.clientIdString = clientIdString;
    if (clientIdString != null) {
      this.clientId = Long.parseLong(clientIdString);
    }
  }

  /** 服务名称 */
  private String serviceName;

  /** 服务主机 */
  private String serviceHost;

  /** 服务端口 */
  private Integer servicePort;

  /** 期望端口 */
  private Integer expectPort;

  /** 服务状态 */
  private String serviceStatus;

  /** 服务协议 */
  private String serviceProtocol;

  /** 服务模式 */
  private String serviceMode;

  /** 服务唯一id */
  private String serviceUniqueId;

  /** 所属服务端id */
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
}
