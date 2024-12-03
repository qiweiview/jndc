package com.view.jndc.server.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.view.jndc.server.utils.SnowflakeIdWorker;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Slf4j
public abstract class TraceableEntity {
    @TableId(value = "id")
    private Long id;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(value = "create_date")
    private LocalDateTime createDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @TableField(value = "update_date")
    private LocalDateTime updateDate;

    @TableField(exist = false)
    private String idS;

    @TableField(exist = false)
    private List<String> idSs;

    @TableField(exist = false)
    private Integer current;

    @TableField(exist = false)
    private Integer size;

    public void tobeResponse() {
      if (id != null) {
          idS = id.toString();
      }
    }

    public void tobeRequest() {
       if (idS != null) {
           id = Long.parseLong(idS);
       }
    }

    public void init() {
        setId(SnowflakeIdWorker.GLOBAL_INSTANCE.nextId());
        setCreateDate(LocalDateTime.now());
        updateOperation();
    }

    public void updateOperation() {
        setUpdateDate(LocalDateTime.now());
    }

}
