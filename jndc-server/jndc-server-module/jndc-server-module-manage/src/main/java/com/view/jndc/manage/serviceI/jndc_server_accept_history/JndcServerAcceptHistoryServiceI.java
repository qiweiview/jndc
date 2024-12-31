package com.view.jndc.manage.serviceI.jndc_server_accept_history;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.jndc.manage.model.jndc_server_accept_history.d_o.JndcServerAcceptHistoryDO;
import com.view.jndc.manage.model.jndc_server_accept_history.dto.JndcServerAcceptHistoryDTO;
import com.view.jndc.manage.model.jndc_server_accept_history.vo.JndcServerAcceptHistoryVO;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

public interface JndcServerAcceptHistoryServiceI {
  IPage<JndcServerAcceptHistoryVO> queryPage(JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO);

  List<JndcServerAcceptHistoryVO> queryList(JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO);

  JndcServerAcceptHistoryDO save(JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO);

  void updateById(JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO);

  void removeById(Serializable id);

  JndcServerAcceptHistoryDTO getById(Serializable id);

    void resetAllAcceptHistory();

    void updateDisconnectTime(String clientId, LocalDateTime now);

    void updateLatestHeartBeatTime(String clientId, LocalDateTime now);

}
