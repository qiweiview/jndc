package com.view.jndc.manage.dao.jndc_server_app;

import com.view.jndc.manage.model.jndc_server_app.d_o.JndcServerAppDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcServerAppDao extends BaseMapper<JndcServerAppDO> {
  IPage<JndcServerAppDO> listPage(Page page, @Param("do") JndcServerAppDO jndcServerAppDO);

  List<JndcServerAppDO> list(JndcServerAppDO jndcServerAppDO);
}
