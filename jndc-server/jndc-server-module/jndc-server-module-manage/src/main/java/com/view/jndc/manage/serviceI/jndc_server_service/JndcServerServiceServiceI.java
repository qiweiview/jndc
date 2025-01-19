package com.view.jndc.manage.serviceI.jndc_server_service;

import com.view.jndc.manage.model.jndc_server_service.vo.JndcServerServiceVO;
import com.view.jndc.manage.model.jndc_server_service.d_o.JndcServerServiceDO;
import com.view.jndc.manage.model.jndc_server_service.dto.JndcServerServiceDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.io.Serializable;

public interface JndcServerServiceServiceI {
  IPage<JndcServerServiceVO> queryPage(JndcServerServiceDTO jndcServerServiceDTO);

  List<JndcServerServiceVO> queryList(JndcServerServiceDTO jndcServerServiceDTO);

  JndcServerServiceDO save(JndcServerServiceDTO jndcServerServiceDTO);

  void updateById(JndcServerServiceDTO jndcServerServiceDTO);

  void removeById(Serializable id);

  JndcServerServiceDTO getById(Serializable id);
}
