package com.view.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.view.dao.entity.SysLoginLog;
import com.view.dao.mapper.SysLoginLogMapper;
import com.view.event.LoginLogEvent;
import com.view.service.SysLoginLogService;
import com.view.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 系统访问记录(SysLoginLog)表服务实现类
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Service
@RequiredArgsConstructor
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {

    private final SysLoginLogMapper loginLogMapper;

    /**
     * 登录日志记录
     *
     */
    @Async
    @EventListener
    public void recordLogin(LoginLogEvent loginLogEvent) {
        SysLoginLog loginLog = BeanCopyUtils.copyBean(loginLogEvent,SysLoginLog.class);
        loginLogMapper.insert(loginLog);
    }
}

