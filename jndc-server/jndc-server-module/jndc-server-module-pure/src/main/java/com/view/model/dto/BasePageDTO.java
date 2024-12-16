package com.view.model.dto;

import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-01 2:59
 * @description: 基本查询分页
 */
@Data
public class BasePageDTO {

    private Long current;

    private Long size;
}
