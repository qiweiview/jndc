package com.view.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.view.dao.entity.SysDict;
import com.view.dao.mapper.SysDictMapper;
import com.view.enums.StatusCodeEnum;
import com.view.exception.ServiceException;
import com.view.service.SysDictService;
import com.view.vo.dict.DictAndDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典表(SysDict)表服务实现类
 *
 * @author sjh
 * @since 2024-04-24 10:35:55
 */
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    private final SysDictMapper sysDictMapper;

    /**
     * 创建字典
     *
     * @param sysDict 字典对象
     * @return 字典ID
     */
    @Override
    public Long createDict(SysDict sysDict) {
        validateDictUniqueness(null, sysDict.getDictName(), sysDict.getDictCode());
        sysDict.setCreateBy(Long.valueOf(StpUtil.getLoginId().toString()));
        sysDictMapper.insert(sysDict);
        return sysDict.getId();
    }

    /**
     * 更新字典
     *
     * @param sysDict 字典对象
     * @return 字典ID
     */
    @Override
    public Long updateDict(SysDict sysDict) {
        SysDict exists = sysDictMapper.selectById(sysDict.getId());
        if (exists == null) {
            throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "修改的字典不存在");
        }
        validateDictUniqueness(sysDict.getId(), sysDict.getDictName(), sysDict.getDictCode());
        sysDict.setUpdateBy(Long.valueOf(StpUtil.getLoginId().toString()));
        sysDictMapper.updateById(sysDict);
        return sysDict.getId();
    }

    @Override
    public List<DictAndDataVO> listAllDictAndData() {
        return sysDictMapper.listAllDictAndData();
    }

    /**
     * 校验字典名称和编码的唯一性
     *
     * @param id   字典ID，用于排除自身
     * @param name 字典名称
     * @param code 字典编码
     */
    private void validateDictUniqueness(Long id, String name, String code) {
        SysDict sysDictName = sysDictMapper.selectOne(new LambdaQueryWrapper<SysDict>().eq(SysDict::getDictName, name));
        if (sysDictName != null && !sysDictName.getId().equals(id)) {
            throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "字典名称不能重复");
        }
        SysDict sysDictCode = sysDictMapper.selectOne(new LambdaQueryWrapper<SysDict>().eq(SysDict::getDictCode, code));
        if (sysDictCode != null && !sysDictCode.getId().equals(id)) {
            throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "字典编码不能重复");
        }
    }


}

