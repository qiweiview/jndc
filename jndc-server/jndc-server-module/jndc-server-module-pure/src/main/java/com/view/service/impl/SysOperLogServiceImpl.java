package com.view.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.view.dao.entity.SysOperLog;
import com.view.dao.mapper.SysOperLogMapper;
import com.view.event.OperLogEvent;
import com.view.service.SysOperLogService;
import com.view.utils.AddressUtils;
import com.view.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志记录表(SysOperLog)表服务实现类
 */
@Service
@RequiredArgsConstructor
public class SysOperLogServiceImpl extends ServiceImpl<SysOperLogMapper, SysOperLog> implements SysOperLogService {


    private final SysOperLogMapper operLogMapper;


    /**
     * 操作日志记录
     *
     * @param operLogEvent 操作日志事件
     */
    @Async
    @EventListener
    public void recordOper(OperLogEvent operLogEvent) {
        SysOperLog operLog = BeanCopyUtils.copyBean(operLogEvent,SysOperLog.class);
        // 远程查询操作地点
        operLog.setOperLocation(AddressUtils.getRealAddressByIP(operLog.getOperIp()));
        operLogMapper.insert(operLog);
    }
}

