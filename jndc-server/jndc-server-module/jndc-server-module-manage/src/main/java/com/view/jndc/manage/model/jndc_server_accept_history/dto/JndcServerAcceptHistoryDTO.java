package com.view.jndc.manage.model.jndc_server_accept_history.dto;

import com.view.jndc.manage.model.jndc_server_accept_history.JndcServerAcceptHistoryStructMapper;
import com.view.jndc.manage.model.jndc_server_accept_history.d_o.JndcServerAcceptHistoryDO;
import com.view.jndc.manage.model.jndc_server_accept_history.vo.JndcServerAcceptHistoryVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class JndcServerAcceptHistoryDTO implements Serializable {

  /** 客户端id */
  private Long clientId;

    /**
     * 客户端id
     */
    private String clientIds;

  /** 连接时间 */
  private java.time.LocalDateTime connectTime;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

    /** id */
  private Long id;

    /**
     * id
     */
    private String ids;

    /** 字符id（处理浏览器long精度丢失问题） */
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

  /** 中断时间 */
  private java.time.LocalDateTime interruptTime;

    /**
     * 服务id
     */
    private Long serverId;

    /**
     * 服务id
     */
    private String serverIds;

    /**
     * 来源ip
     */
    private String sourceIp;

  /** 来源端口 */
  private Integer sourcePort;

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
