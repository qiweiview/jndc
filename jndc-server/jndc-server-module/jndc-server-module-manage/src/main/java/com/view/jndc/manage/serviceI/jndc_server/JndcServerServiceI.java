package com.view.jndc.manage.serviceI.jndc_server;

import com.view.jndc.manage.model.jndc_server.vo.JndcServerVO;
import com.view.jndc.manage.model.jndc_server.d_o.JndcServerDO;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.io.Serializable;

public interface JndcServerServiceI {
  IPage<JndcServerVO> queryPage(JndcServerDTO jndcServerDTO);

  List<JndcServerVO> queryList(JndcServerDTO jndcServerDTO);

  JndcServerDO save(JndcServerDTO jndcServerDTO);

  void updateById(JndcServerDTO jndcServerDTO);

  void removeById(Serializable id);

  JndcServerDTO getById(Serializable id);
}
