package com.view.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.view.dao.entity.SysConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 参数配置表(SysConfig)表数据库访问层
 *
 * @author sjh
 * @since 2024-08-02 14:22:19
 */
@Mapper
public interface SysConfigMapper extends BaseMapper<SysConfig> {

}

