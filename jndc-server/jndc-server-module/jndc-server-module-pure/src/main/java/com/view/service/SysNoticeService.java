package com.view.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.view.dao.entity.SysNotice;
import com.view.dto.notice.NoticeCreateDTO;
import com.view.dto.notice.NoticeQueryDTO;
import com.view.dto.notice.NoticeUpdateDTO;
import com.view.vo.notice.NoticeDetailVO;
import com.view.vo.notice.NoticePageVO;
import com.view.vo.notice.NoticeSimpleVO;

import java.util.List;

/**
 * 通知公告表(SysNotice)表服务接口
 *
 * @author sjh
 * @since 2024-08-04 20:22:28
 */
public interface SysNoticeService extends IService<SysNotice> {

    /**
     * 查询带有一小部分内容的通知
     */
    IPage<NoticeSimpleVO> pageNotice(NoticeQueryDTO query);

    /**
     * 创建通知
     */
    Long createNotice(NoticeCreateDTO createDTO);

    /**
     * 修改公告
     */
    Long updateNotice(NoticeUpdateDTO updateDTO);

    /**
     * 通知详情
     */
    NoticeDetailVO getNotice(Long id);

    /**
     * 删除通知
     */
    Boolean deleteNotices(List<Long> idList);

    /**
     * 根据角色查询通知
     */
    NoticePageVO pageNoticeByUser(NoticeQueryDTO query);

    /**
     * 设为已读
     */
    Boolean setRead(Long noticeId);
}

