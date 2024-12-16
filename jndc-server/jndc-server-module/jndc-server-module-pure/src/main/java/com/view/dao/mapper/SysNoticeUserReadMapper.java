package com.view.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.view.dao.entity.SysNoticeUserRead;
import com.view.vo.notice.NoticeCountVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * (SysNoticeUserRead)表数据库访问层
 *
 * @author sjh
 * @since 2024-08-16 14:27:14
 */
@Mapper
public interface SysNoticeUserReadMapper extends BaseMapper<SysNoticeUserRead> {

    NoticeCountVO countTotalAndUnReadTotal(Long userId);

}

