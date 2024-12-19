package com.view.jndc.manage.serviceI.jndc_log;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.view.jndc.manage.model.jndc_log.d_o.JndcLogDO;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.model.jndc_log.vo.JndcLogVO;

import java.io.Serializable;
import java.util.List;

public interface JndcLogServiceI {
    IPage<JndcLogVO> queryPage(JndcLogDTO jndcLogDTO);

    List<JndcLogVO> queryList(JndcLogDTO jndcLogDTO);

    JndcLogDO save(JndcLogDTO jndcLogDTO);

    void updateById(JndcLogDTO jndcLogDTO);

    void removeById(Serializable id);

    JndcLogDTO getById(Serializable id);
}
