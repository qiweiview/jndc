package com.view.jndc.manage.dao.jndc_clietn_service;

import com.view.jndc.manage.model.jndc_clietn_service.d_o.JndcClietnServiceDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcClietnServiceDao extends BaseMapper<JndcClietnServiceDO> {
  IPage<JndcClietnServiceDO> listPage(
      Page page, @Param("do") JndcClietnServiceDO jndcClietnServiceDO);

  List<JndcClietnServiceDO> list(JndcClietnServiceDO jndcClietnServiceDO);
}
