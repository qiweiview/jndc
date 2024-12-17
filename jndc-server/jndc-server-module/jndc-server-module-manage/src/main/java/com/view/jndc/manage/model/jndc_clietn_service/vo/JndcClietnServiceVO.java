package com.view.jndc.manage.model.jndc_clietn_service.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class JndcClietnServiceVO {

  /** 是否自动注册 */
  private Integer autoRegister;

  /** 所属客户端id */
  private Long belongClientId;

  /** 客户端唯一id */
  private String clientUniqueId;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** */
  private Long id;

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
}
