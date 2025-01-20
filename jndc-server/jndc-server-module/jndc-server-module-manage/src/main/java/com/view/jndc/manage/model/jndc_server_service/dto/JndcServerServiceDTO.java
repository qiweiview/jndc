package com.view.jndc.manage.model.jndc_server_service.dto;

import com.view.jndc.manage.model.jndc_server_service.JndcServerServiceStructMapper;
import com.view.jndc.manage.model.jndc_server_service.d_o.JndcServerServiceDO;
import com.view.jndc.manage.model.jndc_server_service.vo.JndcServerServiceVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class JndcServerServiceDTO implements Serializable {

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

  /** id */
  private String ids;

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

  /** 一页页的条数 */
  private Long size;

  /** 当前页码 */
  protected Long current;

  public JndcServerServiceDO toDO() {
    return JndcServerServiceStructMapper.INSTANCE.toDO(this);
  }

  public JndcServerServiceVO toVO() {
    return JndcServerServiceStructMapper.INSTANCE.toVO(this);
  }
}
