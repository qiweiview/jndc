package com.view.jndc.manage.serviceI.jndc_rule_ip;

import com.view.jndc.manage.model.jndc_rule_ip.vo.JndcRuleIpVO;
import com.view.jndc.manage.model.jndc_rule_ip.d_o.JndcRuleIpDO;
import com.view.jndc.manage.model.jndc_rule_ip.dto.JndcRuleIpDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.io.Serializable;

public interface JndcRuleIpServiceI {
  IPage<JndcRuleIpVO> queryPage(JndcRuleIpDTO jndcRuleIpDTO);

  List<JndcRuleIpVO> queryList(JndcRuleIpDTO jndcRuleIpDTO);

  JndcRuleIpDO save(JndcRuleIpDTO jndcRuleIpDTO);

  void updateById(JndcRuleIpDTO jndcRuleIpDTO);

  void removeById(Serializable id);

  JndcRuleIpDTO getById(Serializable id);
}
