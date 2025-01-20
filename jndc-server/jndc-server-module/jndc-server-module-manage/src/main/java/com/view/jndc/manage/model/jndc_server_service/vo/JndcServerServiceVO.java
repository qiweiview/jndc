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

  /** 客户端唯一id */
  private String clientUniqueId;

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

  /** 服务器唯一id */
  private String serverUniqueId;
}
