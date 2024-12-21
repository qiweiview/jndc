package com.view.jndc.manage.dao.jndc_rule_time;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.jndc.manage.model.jndc_rule_time.d_o.JndcRuleTimeDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcRuleTimeDao extends BaseMapper<JndcRuleTimeDO> {
    IPage<JndcRuleTimeDO> listPage(Page page, @Param("do") JndcRuleTimeDO jndcRuleTimeDO);

    List<JndcRuleTimeDO> list(JndcRuleTimeDO jndcRuleTimeDO);
}
