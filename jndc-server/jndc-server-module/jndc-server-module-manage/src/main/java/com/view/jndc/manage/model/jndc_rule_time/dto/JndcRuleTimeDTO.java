package com.view.jndc.manage.model.jndc_rule_time.dto;

import com.view.jndc.manage.model.jndc_rule_time.JndcRuleTimeStructMapper;
import com.view.jndc.manage.model.jndc_rule_time.d_o.JndcRuleTimeDO;
import com.view.jndc.manage.model.jndc_rule_time.vo.JndcRuleTimeVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class JndcRuleTimeDTO implements Serializable {

    /**
     * 所属id
     */
    private Long belongId;

    /**
     * 所属id
     */
    private String belongIds;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * 截至时间
     */
    private java.time.LocalDateTime effectEndTime;

    /**
     * 起始时间
     */
    private java.time.LocalDateTime effectStartTime;

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private String ids;

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
     * 规则名称
     */
    private String ruleName;

    /**
     * 是否生效
     */
    private String ruleStatus;

    /**
     * 规则类型
     */
    private String ruleType;

    /**
     * 修改时间
     */
    private java.time.LocalDateTime updateTime;

    /**
     * 一页页的条数
     */
    private Long size;

    /**
     * 当前页码
     */
    protected Long current;

    public JndcRuleTimeDO toDO() {
        return JndcRuleTimeStructMapper.INSTANCE.toDO(this);
    }

    public JndcRuleTimeVO toVO() {
        return JndcRuleTimeStructMapper.INSTANCE.toVO(this);
    }
}
