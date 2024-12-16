package com.view.dao.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通知角色表(SysNoticeRole)表实体类
 *
 * @author sjh
 * @since 2024-08-04 20:22:33
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysNoticeRole  {
    /**
    * ID
    */
    @TableId
    private Long id;

    /**
    * 通知ID，只有在通知公告类型为通知时才存
    */
    private Long noticeId;

    /**
    * 要通知的角色
    */
    private Long roleId;




}

