package com.view.vo.notice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-17 14:16
 * @description: 个人通知公告自定义分页
 */
@Getter
@Setter
@NoArgsConstructor
public class NoticePageVO<T> extends Page<T> {

    private NoticeCountVO countVO;


    public NoticePageVO(long current, long size) {
        super(current, size);
    }

}
