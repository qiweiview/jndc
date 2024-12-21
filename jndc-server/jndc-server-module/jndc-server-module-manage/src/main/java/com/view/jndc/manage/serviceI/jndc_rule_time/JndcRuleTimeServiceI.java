package com.view.jndc.manage.serviceI.jndc_rule_time;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.jndc.manage.model.jndc_rule_time.d_o.JndcRuleTimeDO;
import com.view.jndc.manage.model.jndc_rule_time.dto.JndcRuleTimeDTO;
import com.view.jndc.manage.model.jndc_rule_time.vo.JndcRuleTimeVO;

import java.io.Serializable;
import java.util.List;

public interface JndcRuleTimeServiceI {
    IPage<JndcRuleTimeVO> queryPage(JndcRuleTimeDTO jndcRuleTimeDTO);

    List<JndcRuleTimeVO> queryList(JndcRuleTimeDTO jndcRuleTimeDTO);

    JndcRuleTimeDO save(JndcRuleTimeDTO jndcRuleTimeDTO);

    void updateById(JndcRuleTimeDTO jndcRuleTimeDTO);

    void removeById(Serializable id);

    JndcRuleTimeDTO getById(Serializable id);
}
