package com.view.jndc.server.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.view.jndc.server.utils.SnowflakeIdWorker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Data
@Slf4j
public abstract class TraceableEntity {
    @TableId(value = "id")
    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @TableField(value = "create_date")
    private LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @TableField(value = "update_date")
    private LocalDateTime updateDate;

    @TableField(exist = false)
    private Integer current;

    @TableField(exist = false)
    private Integer size;


    public void init() {
        setId(SnowflakeIdWorker.GLOBAL_INSTANCE.nextId());
        setCreateDate(LocalDateTime.now());
    }

    public void update() {
        setUpdateDate(LocalDateTime.now());
    }

}
