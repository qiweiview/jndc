package com.view.jndc.manage.dao.jndc_access_history;

import com.view.jndc.manage.model.jndc_access_history.d_o.JndcAccessHistoryDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcAccessHistoryDao extends BaseMapper<JndcAccessHistoryDO> {
  IPage<JndcAccessHistoryDO> listPage(
      Page page, @Param("do") JndcAccessHistoryDO jndcAccessHistoryDO);

  List<JndcAccessHistoryDO> list(JndcAccessHistoryDO jndcAccessHistoryDO);
}
