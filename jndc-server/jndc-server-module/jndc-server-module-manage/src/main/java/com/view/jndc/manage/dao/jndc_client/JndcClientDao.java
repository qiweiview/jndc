package com.view.jndc.manage.dao.jndc_client;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.jndc.manage.model.jndc_client.d_o.JndcClientDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface JndcClientDao extends BaseMapper<JndcClientDO> {
    IPage<JndcClientDO> listPage(Page page, @Param("do") JndcClientDO jndcClientDO);

    List<JndcClientDO> list(JndcClientDO jndcClientDO);
}
