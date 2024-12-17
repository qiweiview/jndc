package com.view.jndc.manage.model.jndc_client.dto;

import com.view.jndc.manage.model.jndc_client.JndcClientStructMapper;
import com.view.jndc.manage.model.jndc_client.d_o.JndcClientDO;
import com.view.jndc.manage.model.jndc_client.vo.JndcClientVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class JndcClientDTO implements Serializable {

  /** 服务主机 */
  private String bindServerHost;

  /** 服务端口 */
  private Integer bindServerPort;

  /** 客户端名称 */
  private String clientName;

  /** 客户端备注 */
  private String clientRemark;

  /** 客户端状态 */
  private String clientStatus;

  /** 客户端唯一编号 */
  private String clientUniqueId;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** 伪装协议 */
  private String disguisedProtocol;

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

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;

  /** 一页页的条数 */
  private Long size;

  /** 当前页码 */
  protected Long current;

  public JndcClientDO toDO() {
    return JndcClientStructMapper.INSTANCE.toDO(this);
  }

  public JndcClientVO toVO() {
    return JndcClientStructMapper.INSTANCE.toVO(this);
  }
}
