package com.view.jndc.manage.model.jndc_clietn_service.dto;

import com.view.jndc.manage.model.jndc_clietn_service.JndcClietnServiceStructMapper;
import com.view.jndc.manage.model.jndc_clietn_service.d_o.JndcClietnServiceDO;
import com.view.jndc.manage.model.jndc_clietn_service.vo.JndcClietnServiceVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class JndcClietnServiceDTO implements Serializable {

  /** 是否自动注册 */
  private Integer autoRegister;

  /** 所属客户端id */
  private Long belongClientId;

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

  /** 所属客户端id */
  private String belongClientIds;

  /** 客户端唯一id */
  private String clientUniqueId;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** */
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

  /** */
  private String ids;

  /** 服务主机 */
  private String servcieHost;

  /** 服务名称 */
  private String serviceName;

  /** 服务端口 */
  private String servicePort;

  /** 服务状态 */
  private String serviceStatus;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;

  /** 一页页的条数 */
  private Long size;

  /** 当前页码 */
  protected Long current;

  public JndcClietnServiceDO toDO() {
    return JndcClietnServiceStructMapper.INSTANCE.toDO(this);
  }

  public JndcClietnServiceVO toVO() {
    return JndcClietnServiceStructMapper.INSTANCE.toVO(this);
  }
}
