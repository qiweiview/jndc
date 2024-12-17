package com.view.jndc.manage.model.jndc_server.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class JndcServerVO {

  /** jndc-server监听端口 */
  private Integer bindPort;

  /** 绑定策略 */
  private String bindTactics;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** */
  private Long id;

  /** 服务名称 */
  private String serverName;

  /** 服务备注 */
  private String serverRemark;

  /** 服务状态 */
  private String serverStatus;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;
}
