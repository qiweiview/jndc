package com.view.jndc.manage.dao.jndc_server_app_bind;

import com.view.jndc.manage.model.jndc_server_app_bind.d_o.JndcServerAppBindDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcServerAppBindDao extends BaseMapper<JndcServerAppBindDO> {
  IPage<JndcServerAppBindDO> listPage(
      Page page, @Param("do") JndcServerAppBindDO jndcServerAppBindDO);

  List<JndcServerAppBindDO> list(JndcServerAppBindDO jndcServerAppBindDO);
}
