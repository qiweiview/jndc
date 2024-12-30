package com.view.jndc.manage.serviceI.jndc_server_app;

import com.view.jndc.manage.model.jndc_server_app.vo.JndcServerAppVO;
import com.view.jndc.manage.model.jndc_server_app.d_o.JndcServerAppDO;
import com.view.jndc.manage.model.jndc_server_app.dto.JndcServerAppDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.io.Serializable;

public interface JndcServerAppServiceI {
  IPage<JndcServerAppVO> queryPage(JndcServerAppDTO jndcServerAppDTO);

  List<JndcServerAppVO> queryList(JndcServerAppDTO jndcServerAppDTO);

  JndcServerAppDO save(JndcServerAppDTO jndcServerAppDTO);

  void updateById(JndcServerAppDTO jndcServerAppDTO);

  void removeById(Serializable id);

  JndcServerAppDTO getById(Serializable id);

  void updateStatusByServiceId(String serviceId, String value);

  JndcServerAppDTO getByServiceId(String serviceId);

}
