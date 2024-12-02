package com.view.jndc.server.dao.admin;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.jndc.server.model.admin.PureUserEntity;
import com.view.jndc.server.model.ndc.server.NDCServerEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminDao extends BaseMapper<PureUserEntity> {
    Page<PureUserEntity> queryUserPage(Page<PureUserEntity> page, @Param("param") PureUserEntity pureUserEntity);

}
