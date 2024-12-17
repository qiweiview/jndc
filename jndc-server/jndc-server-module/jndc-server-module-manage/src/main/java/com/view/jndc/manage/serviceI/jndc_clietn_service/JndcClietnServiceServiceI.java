package com.view.jndc.manage.serviceI.jndc_clietn_service;

import com.view.jndc.manage.model.jndc_clietn_service.vo.JndcClietnServiceVO;
import com.view.jndc.manage.model.jndc_clietn_service.d_o.JndcClietnServiceDO;
import com.view.jndc.manage.model.jndc_clietn_service.dto.JndcClietnServiceDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.io.Serializable;

public interface JndcClietnServiceServiceI {
  IPage<JndcClietnServiceVO> queryPage(JndcClietnServiceDTO jndcClietnServiceDTO);

  List<JndcClietnServiceVO> queryList(JndcClietnServiceDTO jndcClietnServiceDTO);

  JndcClietnServiceDO save(JndcClietnServiceDTO jndcClietnServiceDTO);

  void updateById(JndcClietnServiceDTO jndcClietnServiceDTO);

  void removeById(Serializable id);

  JndcClietnServiceDTO getById(Serializable id);
}
