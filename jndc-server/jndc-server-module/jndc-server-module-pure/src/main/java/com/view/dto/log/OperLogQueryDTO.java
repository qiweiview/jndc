package com.view.dto.log;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.dao.entity.SysOperLog;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-09-14 8:58
 * @description: 操作日志查询
 */
@Data
public class OperLogQueryDTO extends Page<SysOperLog> {

    private String title;

    private Integer status;

    /**
     * 第一个为开始时间，第二个为结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime[] operTimeArr;
}
