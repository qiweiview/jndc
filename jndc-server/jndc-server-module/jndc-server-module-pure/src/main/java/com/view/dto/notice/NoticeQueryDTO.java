package com.view.dto.notice;

import com.view.model.dto.BasePageDTO;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-04 20:53
 * @description: 通知查询
 */
@Data
public class NoticeQueryDTO extends BasePageDTO {
    private Long id;

    private String title;

    private Integer type;

    private Integer status;
}
