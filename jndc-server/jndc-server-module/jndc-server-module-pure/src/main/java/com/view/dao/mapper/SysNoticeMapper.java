package com.view.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.dao.entity.SysNotice;
import com.view.dto.notice.NoticeQueryDTO;
import com.view.vo.notice.NoticeDetailVO;
import com.view.vo.notice.NoticePageVO;
import com.view.vo.notice.NoticeSimpleVO;
import com.view.vo.notice.NoticeUserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 通知公告表(SysNotice)表数据库访问层
 *
 * @author sjh
 * @since 2024-08-04 20:22:23
 */
@Mapper
public interface SysNoticeMapper extends BaseMapper<SysNotice> {

    /**
     * 自定义分页查询
     * @param page 分页对象
     * @param query 查询对象
     * @return 查询结果
     */
    IPage<NoticeSimpleVO> selectNoticeVOPage(IPage<NoticeSimpleVO> page, @Param("query") NoticeQueryDTO query);

    NoticeDetailVO getNoticeById(Long id);

    NoticePageVO<NoticeUserVO> selectNoticeByUser(Page<NoticeUserVO> page,
                                    @Param("query") NoticeQueryDTO query,
                                    @Param("userId") Long userId);

}

