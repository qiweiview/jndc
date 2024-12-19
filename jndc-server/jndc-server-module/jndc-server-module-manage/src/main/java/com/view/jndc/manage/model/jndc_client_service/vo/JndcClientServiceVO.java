package com.view.jndc.manage.model.jndc_client_service.vo;

import lombok.Data;

@Data
public class JndcClientServiceVO {

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

    /**
     * 字符id（处理浏览器long精度丢失问题）
     */
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

    /**
     * 服务主机
     */
    private String serviceHost;

  /** 服务名称 */
  private String serviceName;

  /** 服务端口 */
  private String servicePort;

  /** 服务状态 */
  private String serviceStatus;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;
}
