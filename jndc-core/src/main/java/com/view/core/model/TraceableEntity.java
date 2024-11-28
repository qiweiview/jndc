package com.view.core.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Data
@Slf4j
public abstract class TraceableEntity {
    private Long id;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;


}
