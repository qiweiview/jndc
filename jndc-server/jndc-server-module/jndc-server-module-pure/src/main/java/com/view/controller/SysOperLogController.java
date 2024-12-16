package com.view.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.annotation.AdminPrefix;
import com.view.annotation.OperationLog;
import com.view.dao.entity.SysOperLog;
import com.view.dto.log.OperLogQueryDTO;
import com.view.enums.OperBusinessType;
import com.view.model.vo.ResponseResult;
import com.view.service.SysOperLogService;
import com.view.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Objects;

@AdminPrefix
@RequestMapping("/sysOperLog")
@RequiredArgsConstructor
public class SysOperLogController {
    /**
     * 服务对象
     */
    private final SysOperLogService operLogService;

    /**
     * 分页查询所有数据
     *
     * @return 所有数据
     */
    @GetMapping("/list")
    public ResponseResult<Page<SysOperLog>> selectAll(OperLogQueryDTO queryDTO) {
        LambdaQueryWrapper<SysOperLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(queryDTO.getTitle()), SysOperLog::getTitle, queryDTO.getTitle());
        if (queryDTO.getOperTimeArr().length == 2) {
            queryWrapper.between(SysOperLog::getOperTime, queryDTO.getOperTimeArr()[0], queryDTO.getOperTimeArr()[1]);
        }

        queryWrapper.eq(Objects.nonNull(queryDTO.getStatus()), SysOperLog::getStatus, queryDTO.getStatus());
        return ResponseResult.ok(operLogService.page(queryDTO, queryWrapper));
    }


    /**
     * 删除数据
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @OperationLog(title = "操作日志管理", businessType = OperBusinessType.DELETE)
    @DeleteMapping("/delete")
    public ResponseResult<Boolean> delete(@RequestBody List<Long> idList) {
        return ResponseResult.ok(operLogService.removeByIds(idList));
    }

    /**
     * 清空日志
     * @return 删除结果
     */
    @OperationLog(title = "操作日志管理", businessType = OperBusinessType.CLEAR)
    @DeleteMapping("/clear")
    public ResponseResult<Boolean> clear() {
        return ResponseResult.ok(operLogService.remove(null));
    }
}

