package com.view.jndc.manage.dao.jndc_server_accept_history;

import com.view.jndc.manage.model.jndc_server_accept_history.d_o.JndcServerAcceptHistoryDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcServerAcceptHistoryDao extends BaseMapper<JndcServerAcceptHistoryDO> {
  IPage<JndcServerAcceptHistoryDO> listPage(
      Page page, @Param("do") JndcServerAcceptHistoryDO jndcServerAcceptHistoryDO);

  List<JndcServerAcceptHistoryDO> list(JndcServerAcceptHistoryDO jndcServerAcceptHistoryDO);
}
