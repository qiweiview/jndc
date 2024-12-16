package com.view.vo.notice;

import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-18 3:26
 * @description: 通知公告总数与未读数
 */
@Data
public class NoticeCountVO {

    /**
     * 全部通知公告
     */
    private Long totalCount;

    /**
     * 未读
     */
    private Long noReadCount;
}
