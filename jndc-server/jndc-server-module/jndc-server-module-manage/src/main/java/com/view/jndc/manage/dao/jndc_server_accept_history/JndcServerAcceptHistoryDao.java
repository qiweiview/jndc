package com.view.jndc.manage.dao.jndc_server_accept_history;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.jndc.manage.model.jndc_server_accept_history.d_o.JndcServerAcceptHistoryDO;
import com.view.jndc.manage.model.jndc_server_accept_history.dto.JndcServerAcceptHistoryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface JndcServerAcceptHistoryDao extends BaseMapper<JndcServerAcceptHistoryDO> {
    IPage<JndcServerAcceptHistoryDO> listPage(
            Page page, @Param("dto") JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO);

    List<JndcServerAcceptHistoryDO> list(JndcServerAcceptHistoryDO jndcServerAcceptHistoryDO);

    void resetAllAcceptHistory(@Param("now") LocalDateTime now);

}
