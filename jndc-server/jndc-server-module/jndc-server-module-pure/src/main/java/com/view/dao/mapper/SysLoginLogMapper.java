package com.view.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.view.dao.entity.SysLoginLog;
import org.springframework.stereotype.Repository;

/**
 * 系统访问记录(SysLoginLog)表数据库访问层
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Repository
public interface SysLoginLogMapper extends BaseMapper<SysLoginLog> {

}

