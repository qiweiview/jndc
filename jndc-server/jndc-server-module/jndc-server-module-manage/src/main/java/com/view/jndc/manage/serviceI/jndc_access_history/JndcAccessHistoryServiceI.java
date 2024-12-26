package com.view.jndc.manage.serviceI.jndc_access_history;

import com.view.jndc.manage.model.jndc_access_history.vo.JndcAccessHistoryVO;
import com.view.jndc.manage.model.jndc_access_history.d_o.JndcAccessHistoryDO;
import com.view.jndc.manage.model.jndc_access_history.dto.JndcAccessHistoryDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.io.Serializable;

public interface JndcAccessHistoryServiceI {
  IPage<JndcAccessHistoryVO> queryPage(JndcAccessHistoryDTO jndcAccessHistoryDTO);

  List<JndcAccessHistoryVO> queryList(JndcAccessHistoryDTO jndcAccessHistoryDTO);

  JndcAccessHistoryDO save(JndcAccessHistoryDTO jndcAccessHistoryDTO);

  void updateById(JndcAccessHistoryDTO jndcAccessHistoryDTO);

  void removeById(Serializable id);

  JndcAccessHistoryDTO getById(Serializable id);
}
