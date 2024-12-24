package com.view.jndc.manage.model.jndc_rule_ip.d_o;

import com.view.jndc.manage.model.jndc_rule_ip.JndcRuleIpStructMapper;
import com.view.jndc.manage.model.jndc_rule_ip.dto.JndcRuleIpDTO;
import com.view.jndc.manage.model.jndc_rule_ip.vo.JndcRuleIpVO;
import com.baomidou.mybatisplus.annotation.*;
import java.sql.Timestamp;
import lombok.Data;

@TableName("jndc_rule_ip")
@Data
public class JndcRuleIpDO {
  /** 所属id */
  @TableField(value = "belong_id")
  private Long belongId;

  /** 创建时间 */
  @TableField(value = "create_time")
  private java.time.LocalDateTime createTime;

  /** id */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /** ip地址 */
  @TableField(value = "ip_address")
  private String ipAddress;

  /** 规则名称 */
  @TableField(value = "rule_name")
  private String ruleName;

  /** 是否生效 */
  @TableField(value = "rule_status")
  private String ruleStatus;

  /** 规则类型 */
  @TableField(value = "rule_type")
  private String ruleType;

  /** 采样长度 */
  @TableField(value = "sampling_length")
  private Integer samplingLength;

  /** 修改时间 */
  @TableField(value = "update_time")
  private java.time.LocalDateTime updateTime;

  public JndcRuleIpDTO toDTO() {
    return JndcRuleIpStructMapper.INSTANCE.toDTO(this);
  }

  public JndcRuleIpVO toVO() {
    return JndcRuleIpStructMapper.INSTANCE.toVO(this);
  }
}
