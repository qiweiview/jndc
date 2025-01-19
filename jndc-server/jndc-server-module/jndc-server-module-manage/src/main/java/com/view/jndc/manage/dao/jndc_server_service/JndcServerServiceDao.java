package com.view.jndc.manage.dao.jndc_server_service;

import com.view.jndc.manage.model.jndc_server_service.d_o.JndcServerServiceDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcServerServiceDao extends BaseMapper<JndcServerServiceDO> {
  IPage<JndcServerServiceDO> listPage(
      Page page, @Param("do") JndcServerServiceDO jndcServerServiceDO);

  List<JndcServerServiceDO> list(JndcServerServiceDO jndcServerServiceDO);
}
