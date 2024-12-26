package com.view.jndc.manage.model.jndc_access_history.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class JndcAccessHistoryVO {

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** 访问目标 */
  private String destination;

  /** 目标id */
  private Long destinationId;

  /** id */
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

  /** 数据采样 */
  private String packageSampling;

  /** ip地址 */
  private String remoteIp;

  /** 端口 */
  private Integer remotePort;
}
