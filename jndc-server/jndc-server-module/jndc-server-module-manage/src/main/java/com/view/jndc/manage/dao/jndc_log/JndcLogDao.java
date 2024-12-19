package com.view.jndc.manage.dao.jndc_log;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.jndc.manage.model.jndc_log.d_o.JndcLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcLogDao extends BaseMapper<JndcLogDO> {
    IPage<JndcLogDO> listPage(Page page, @Param("do") JndcLogDO jndcLogDO);

    List<JndcLogDO> list(JndcLogDO jndcLogDO);
}
