package com.view.jndc.manage.model.jndc_rule_ip.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class JndcRuleIpVO {

  /** 所属id */
  private Long belongId;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

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

  /** ip地址 */
  private String ipAddress;

  /** 规则名称 */
  private String ruleName;

  /** 是否生效 */
  private String ruleStatus;

  /** 规则类型 */
  private String ruleType;

  /** 采样长度 */
  private Integer samplingLength;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;
}
