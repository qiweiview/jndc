package com.view.jndc.manage.model.jndc_server_accept_history.dto;

import com.view.jndc.manage.model.jndc_server_accept_history.JndcServerAcceptHistoryStructMapper;
import com.view.jndc.manage.model.jndc_server_accept_history.d_o.JndcServerAcceptHistoryDO;
import com.view.jndc.manage.model.jndc_server_accept_history.vo.JndcServerAcceptHistoryVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class JndcServerAcceptHistoryDTO implements Serializable {

  /** 客户端id */
  private String clientId;

  /** 连接时间 */
  private java.time.LocalDateTime connectTime;

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

  /** 中断时间 */
  private java.time.LocalDateTime interruptTime;

  /** 来源ip */
  private String sourceIp;

  /** 来源端口 */
  private String sourcePort;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;

  /** 一页页的条数 */
  private Long size;

  /** 当前页码 */
  protected Long current;

  public JndcServerAcceptHistoryDO toDO() {
    return JndcServerAcceptHistoryStructMapper.INSTANCE.toDO(this);
  }

  public JndcServerAcceptHistoryVO toVO() {
    return JndcServerAcceptHistoryStructMapper.INSTANCE.toVO(this);
  }
}
