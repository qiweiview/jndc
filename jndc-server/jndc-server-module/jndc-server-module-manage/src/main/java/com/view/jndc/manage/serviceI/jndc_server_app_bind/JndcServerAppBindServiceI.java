package com.view.jndc.manage.serviceI.jndc_server_app_bind;

import com.view.jndc.manage.model.jndc_server_app_bind.vo.JndcServerAppBindVO;
import com.view.jndc.manage.model.jndc_server_app_bind.d_o.JndcServerAppBindDO;
import com.view.jndc.manage.model.jndc_server_app_bind.dto.JndcServerAppBindDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import java.util.List;
import java.io.Serializable;

public interface JndcServerAppBindServiceI {
  IPage<JndcServerAppBindVO> queryPage(JndcServerAppBindDTO jndcServerAppBindDTO);

  List<JndcServerAppBindVO> queryList(JndcServerAppBindDTO jndcServerAppBindDTO);

  JndcServerAppBindDO save(JndcServerAppBindDTO jndcServerAppBindDTO);

  void updateById(JndcServerAppBindDTO jndcServerAppBindDTO);

  void removeById(Serializable id);

  JndcServerAppBindDTO getById(Serializable id);
}
