package com.view.jndc.server.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.view.jndc.server.utils.SnowflakeIdWorker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Data
@Slf4j
public abstract class TraceableEntity {
    @TableId(value = "id")
    private Long id;

    @TableField(value = "create_date")
    private LocalDateTime createDate;

    @TableField(value = "update_date")
    private LocalDateTime updateDate;


    public void init() {
        setId(SnowflakeIdWorker.GLOBAL_INSTANCE.nextId());
        setCreateDate(LocalDateTime.now());
    }

    public void update() {
        setUpdateDate(LocalDateTime.now());
    }

}
