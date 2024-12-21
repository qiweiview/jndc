package com.view.jndc.manage.model.jndc_rule_time.d_o;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.manage.model.jndc_rule_time.JndcRuleTimeStructMapper;
import com.view.jndc.manage.model.jndc_rule_time.dto.JndcRuleTimeDTO;
import com.view.jndc.manage.model.jndc_rule_time.vo.JndcRuleTimeVO;
import lombok.Data;

@TableName("jndc_rule_time")
@Data
public class JndcRuleTimeDO {
    /**
     * 所属id
     */
    @TableField(value = "belong_id")
    private Long belongId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private java.time.LocalDateTime createTime;

    /**
     * 截至时间
     */
    @TableField(value = "effect_end_time")
    private java.time.LocalDateTime effectEndTime;

    /**
     * 起始时间
     */
    @TableField(value = "effect_start_time")
    private java.time.LocalDateTime effectStartTime;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规则名称
     */
    @TableField(value = "rule_name")
    private String ruleName;

    /**
     * 是否生效
     */
    @TableField(value = "rule_status")
    private String ruleStatus;

    /**
     * 规则类型
     */
    @TableField(value = "rule_type")
    private String ruleType;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private java.time.LocalDateTime updateTime;

    public JndcRuleTimeDTO toDTO() {
        return JndcRuleTimeStructMapper.INSTANCE.toDTO(this);
    }

    public JndcRuleTimeVO toVO() {
        return JndcRuleTimeStructMapper.INSTANCE.toVO(this);
    }
}
