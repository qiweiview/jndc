package com.view.jndc.manage.model.jndc_server_app_bind.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class JndcServerAppBindVO {

  /** 绑定端口 */
  private Integer bindPort;

  /** 绑定来源 */
  private String bindSource;

  /** 绑定状态 */
  private String bindStatus;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** */
  private Long id;

  /** 最后绑定结果 */
  private String latestBindResult;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;
}
