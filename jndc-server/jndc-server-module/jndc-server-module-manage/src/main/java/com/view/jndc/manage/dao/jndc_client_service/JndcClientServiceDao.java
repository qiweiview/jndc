package com.view.jndc.manage.dao.jndc_client_service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.jndc.manage.model.jndc_client_service.d_o.JndcClientServiceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcClientServiceDao extends BaseMapper<JndcClientServiceDO> {
    IPage<JndcClientServiceDO> listPage(
            Page page, @Param("do") JndcClientServiceDO jndcClientServiceDO);

    List<JndcClientServiceDO> list(JndcClientServiceDO jndcClientServiceDO);
}
