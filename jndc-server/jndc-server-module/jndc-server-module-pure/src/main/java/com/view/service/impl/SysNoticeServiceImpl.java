package com.view.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.view.dao.entity.*;
import com.view.dao.mapper.*;
import com.view.dto.notice.NoticeCreateDTO;
import com.view.dto.notice.NoticeQueryDTO;
import com.view.dto.notice.NoticeUpdateDTO;
import com.view.enums.NoticeTypeEnum;
import com.view.enums.ReadStatusEnum;
import com.view.exception.ServiceException;
import com.view.service.SysNoticeRoleService;
import com.view.service.SysNoticeService;
import com.view.service.SysNoticeUserReadService;
import com.view.utils.BeanCopyUtils;
import com.view.utils.TimeUtils;
import com.view.vo.notice.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通知公告表(SysNotice)表服务实现类
 *
 * @author sjh
 * @since 2024-08-04 20:22:30
 */
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    private final SysNoticeMapper noticeMapper;

    private final SysNoticeRoleService noticeRoleService;

    private final SysNoticeRoleMapper noticeRoleMapper;

    private final SysUserRoleMapper userRoleMapper;

    private final SysNoticeUserReadService noticeUserReadService;

    private final SysUserMapper userMapper;

    private final SysNoticeUserReadMapper noticeUserReadMapper;

    @Override
    public IPage<NoticeSimpleVO> pageNotice(NoticeQueryDTO query) {
        Page<NoticeSimpleVO> page = new Page<>(query.getCurrent(), query.getSize());
        return noticeMapper.selectNoticeVOPage(page, query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNotice(NoticeCreateDTO createDTO) {
        SysNotice sysNotice = BeanCopyUtils.copyBean(createDTO, SysNotice.class);
        sysNotice.setCreateBy(Long.valueOf(StpUtil.getLoginId().toString()));
        noticeMapper.insert(sysNotice);
        if (sysNotice.getType().equals(NoticeTypeEnum.NOTIFICATION.getType())) {
            // 为通知类型则存入通知角色表
            List<SysNoticeRole> noticeRoleList = new ArrayList<>();
            for (Long roleId : createDTO.getRoleIds()) {
                SysNoticeRole sysNoticeRole = new SysNoticeRole();
                sysNoticeRole.setNoticeId(sysNotice.getId());
                sysNoticeRole.setRoleId(roleId);
                noticeRoleList.add(sysNoticeRole);
            }
            noticeRoleService.saveBatch(noticeRoleList);
            // 对绑定该角色的用户记录阅读
            List<SysUserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getRoleId, createDTO.getRoleIds()));
            // 2. 使用Set去重，确保每个用户ID唯一
            Set<Long> uniqueUserIds = userRoles.stream()
                    .map(SysUserRole::getUserId)
                    .collect(Collectors.toSet());
            List<SysNoticeUserRead> userReads = new ArrayList<>();
            for (Long userId : uniqueUserIds) {
                userReads.add(SysNoticeUserRead.builder().
                        noticeId(sysNotice.getId()).
                        userId(userId).
                        status(ReadStatusEnum.UNREAD.getCode())
                        .build());
            }
            noticeUserReadService.saveBatch(userReads);
        }else{
            // 公告则是所有用户都能看见
            List<SysUser> userList = userMapper.selectList(null);
            List<SysNoticeUserRead> userReads = new ArrayList<>();
            for (SysUser user : userList) {
                userReads.add(SysNoticeUserRead.builder().
                        noticeId(sysNotice.getId()).
                        userId(user.getId()).
                        status(ReadStatusEnum.UNREAD.getCode())
                        .build());
            }
            noticeUserReadService.saveBatch(userReads);
        }
        return sysNotice.getId();
    }

    /**
     * 修改（暂时不让修改通知角色）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long updateNotice(NoticeUpdateDTO updateDTO) {
        SysNotice dbNotice = noticeMapper.selectById(updateDTO.getId());
        if (Objects.isNull(dbNotice)){
            throw new ServiceException("修改的通知公告不存在");
        }
        if (!dbNotice.getType().equals(updateDTO.getType())){
            throw new ServiceException("不可修改通知公告类型");
        }
        SysNotice updateNotice = BeanCopyUtils.copyBean(updateDTO, SysNotice.class);
        updateNotice.setUpdateBy(Long.valueOf(StpUtil.getLoginId().toString()));
        noticeMapper.updateById(updateNotice);
//        if (updateNotice.getType().equals(NoticeTypeEnum.NOTIFICATION.getType())) {
//            // 获得通知角色编号
//            Set<Long> dbRoleIds = convertSet(noticeRoleMapper.selectList(new LambdaQueryWrapper<SysNoticeRole>().eq(SysNoticeRole::getNoticeId, updateDTO.getId())),
//                    SysNoticeRole::getRoleId);
//            // 计算新增和删除的角色编号
//            Set<Long> roleIdList = CollUtil.emptyIfNull(updateDTO.getRoleIds());
//            Collection<Long> createRoleIds = CollUtil.subtract(roleIdList, dbRoleIds);
//            Collection<Long> deleteRoleIds = CollUtil.subtract(dbRoleIds, roleIdList);
//            // 执行新增和删除。
//            if (!CollectionUtil.isEmpty(createRoleIds)) {
//                noticeRoleService.saveBatch(CollectionUtils.convertList(createRoleIds, roleId -> {
//                    SysNoticeRole entity = new SysNoticeRole();
//                    entity.setNoticeId(updateDTO.getId());
//                    entity.setRoleId(roleId);
//                    return entity;
//                }));
//            }
//            if (!CollectionUtil.isEmpty(deleteRoleIds)) {
//                noticeRoleMapper.delete(new LambdaQueryWrapper<SysNoticeRole>().eq(SysNoticeRole::getNoticeId,updateDTO.getId()).in(SysNoticeRole::getRoleId,deleteRoleIds));
//            }
//        }

        return updateNotice.getId();
    }

    @Override
    public NoticeDetailVO getNotice(Long id) {
        return noticeMapper.getNoticeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteNotices(List<Long> idList) {
        this.removeBatchByIds(idList);
        noticeRoleService.remove(new LambdaQueryWrapper<SysNoticeRole>().in(SysNoticeRole::getNoticeId,idList));
        noticeUserReadService.remove(new LambdaQueryWrapper<SysNoticeUserRead>().in(SysNoticeUserRead::getNoticeId,idList));
        return true;
    }

    @Override
    public NoticePageVO<NoticeUserVO> pageNoticeByUser(NoticeQueryDTO query) {
        Long loginId = Long.valueOf(StpUtil.getLoginId().toString());
        NoticePageVO<NoticeUserVO> page = new NoticePageVO<>(query.getCurrent(), query.getSize());
        NoticePageVO<NoticeUserVO> noticePage = noticeMapper.selectNoticeByUser(page, query,loginId);
        // 查全部的通知数与未读数
        NoticeCountVO count = noticeUserReadMapper.countTotalAndUnReadTotal(loginId);
        noticePage.setCountVO(count);
        return noticePage;
    }

    @Override
    public Boolean setRead(Long noticeId) {
        Long loginId = Long.valueOf(StpUtil.getLoginId().toString());
        SysNoticeUserRead noticeUserRead = noticeUserReadMapper.
                selectOne(new LambdaQueryWrapper<SysNoticeUserRead>().
                        eq(SysNoticeUserRead::getUserId, loginId).
                        eq(SysNoticeUserRead::getNoticeId, noticeId));
        if (Objects.isNull(noticeUserRead)){
            throw new ServiceException("记录不存在");
        }
        if (noticeUserRead.getStatus().equals(ReadStatusEnum.READ.getCode())){
            return true;
        }
        noticeUserRead.setStatus(ReadStatusEnum.READ.getCode());
        noticeUserRead.setReadTime( TimeUtils.now());
        noticeUserReadMapper.updateById(noticeUserRead);
        return true;
    }

}

