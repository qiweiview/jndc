package com.view.jndc.manage.serviceI.jndc_client_service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.jndc.manage.model.jndc_client_service.d_o.JndcClientServiceDO;
import com.view.jndc.manage.model.jndc_client_service.dto.JndcClientServiceDTO;
import com.view.jndc.manage.model.jndc_client_service.vo.JndcClientServiceVO;

import java.io.Serializable;
import java.util.List;

public interface JndcClientServiceServiceI {
    IPage<JndcClientServiceVO> queryPage(JndcClientServiceDTO jndcClientServiceDTO);

    List<JndcClientServiceVO> queryList(JndcClientServiceDTO jndcClientServiceDTO);

    JndcClientServiceDO save(JndcClientServiceDTO jndcClientServiceDTO);

    void updateById(JndcClientServiceDTO jndcClientServiceDTO);

    void removeById(Serializable id);

    JndcClientServiceDTO getById(Serializable id);
}
