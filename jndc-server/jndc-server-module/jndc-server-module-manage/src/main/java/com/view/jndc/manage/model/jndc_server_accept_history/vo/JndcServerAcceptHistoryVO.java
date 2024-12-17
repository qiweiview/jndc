package com.view.jndc.manage.model.jndc_server_accept_history.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class JndcServerAcceptHistoryVO {

  /** 客户端id */
  private String clientId;

  /** 连接时间 */
  private java.time.LocalDateTime connectTime;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** */
  private Long id;

  /** 中断时间 */
  private java.time.LocalDateTime interruptTime;

  /** 来源ip */
  private String sourceIp;

  /** 来源端口 */
  private String sourcePort;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;
}
