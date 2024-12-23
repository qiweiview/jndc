package com.view.jndc.manage.serviceI.jndc_client;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.jndc.manage.model.jndc_client.d_o.JndcClientDO;
import com.view.jndc.manage.model.jndc_client.dto.JndcClientDTO;
import com.view.jndc.manage.model.jndc_client.vo.JndcClientVO;

import java.io.Serializable;
import java.util.List;

public interface JndcClientServiceI {
    IPage<JndcClientVO> queryPage(JndcClientDTO jndcClientDTO);

    List<JndcClientVO> queryList(JndcClientDTO jndcClientDTO);

    JndcClientDO save(JndcClientDTO jndcClientDTO);

    void updateById(JndcClientDTO jndcClientDTO);

    void removeById(Serializable id);

    JndcClientDTO getById(Serializable id);

    void resetAllClientStatus();

    void forceStopOperation(Long id);

}
