package com.view.jndc.manage.serviceI.jndc_server_accept_history;

import com.view.jndc.manage.model.jndc_server_accept_history.vo.JndcServerAcceptHistoryVO;
import com.view.jndc.manage.model.jndc_server_accept_history.d_o.JndcServerAcceptHistoryDO;
import com.view.jndc.manage.model.jndc_server_accept_history.dto.JndcServerAcceptHistoryDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.io.Serializable;

public interface JndcServerAcceptHistoryServiceI {
  IPage<JndcServerAcceptHistoryVO> queryPage(JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO);

  List<JndcServerAcceptHistoryVO> queryList(JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO);

  JndcServerAcceptHistoryDO save(JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO);

  void updateById(JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO);

  void removeById(Serializable id);

  JndcServerAcceptHistoryDTO getById(Serializable id);
}
