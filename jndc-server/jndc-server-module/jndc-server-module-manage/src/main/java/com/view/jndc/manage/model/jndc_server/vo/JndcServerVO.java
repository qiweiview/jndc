package com.view.jndc.manage.model.jndc_server.vo;

import lombok.Data;

@Data
public class JndcServerVO {

    /**
     * 监听端口
     */
  private Integer bindPort;

  private String bindHost;

  /** 绑定策略 */
  private String bindTactics;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** */
  private Long id;

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

  /** 服务名称 */
  private String serverName;

  /** 服务备注 */
  private String serverRemark;

  /** 服务状态 */
  private String serverStatus;

    /**
     * 唯一id
     */
    private String uniqueId;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;
}
