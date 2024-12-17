package com.view.jndc.manage.dao.jndc_server;

import com.view.jndc.manage.model.jndc_server.d_o.JndcServerDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcServerDao extends BaseMapper<JndcServerDO> {
  IPage<JndcServerDO> listPage(Page page, @Param("do") JndcServerDO jndcServerDO);

  List<JndcServerDO> list(JndcServerDO jndcServerDO);
}
