package com.view.jndc.manage.dao.jndc_rule_ip;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.jndc.manage.model.jndc_rule_ip.d_o.JndcRuleIpDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcRuleIpDao extends BaseMapper<JndcRuleIpDO> {
    IPage<JndcRuleIpDO> listPage(Page page, @Param("do") JndcRuleIpDO jndcRuleIpDO);

    List<JndcRuleIpDO> list(JndcRuleIpDO jndcRuleIpDO);
}
