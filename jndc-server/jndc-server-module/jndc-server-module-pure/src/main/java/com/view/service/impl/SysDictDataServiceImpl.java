package com.view.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.view.dao.entity.SysDictData;
import com.view.dao.mapper.SysDictDataMapper;
import com.view.enums.StatusCodeEnum;
import com.view.exception.ServiceException;
import com.view.service.SysDictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 字典数据表(SysDictData)表服务实现类
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    private final SysDictDataMapper sysDictDataMapper;

    /**
     * 创建字典
     *
     * @param sysDictData 字典数据项对象
     * @return 字典数据项ID
     */
    @Override
    public Long createDictData(SysDictData sysDictData) {
        validateDictDataUniqueness(null, sysDictData.getName(), sysDictData.getValue(),sysDictData.getDictId());
        sysDictData.setCreateBy(Long.valueOf(StpUtil.getLoginId().toString()));
        sysDictDataMapper.insert(sysDictData);
        return sysDictData.getId();
    }

    /**
     * 更新字典数据项
     *
     * @param sysDictData 字典数据项对象
     * @return 字典数据项ID
     */
    @Override
    public Long updateDictData(SysDictData sysDictData) {
        SysDictData exists = sysDictDataMapper.selectById(sysDictData.getId());
        if (exists == null) {
            throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "修改的字典不存在");
        }
        validateDictDataUniqueness(sysDictData.getId(), sysDictData.getName(), sysDictData.getValue(),sysDictData.getDictId());
        sysDictData.setUpdateBy(Long.valueOf(StpUtil.getLoginId().toString()));
        sysDictDataMapper.updateById(sysDictData);
        return sysDictData.getId();
    }

    /**
     * 校验字典数据项名称和值的唯一性
     *
     * @param id   字典数据项ID，用于排除自身
     * @param name 字典数据项名称
     * @param value 字典数据项值
     */
    private void validateDictDataUniqueness(Long id, String name, String value,Long dictId) {
        SysDictData sysDictDataName = sysDictDataMapper.selectOne(new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getName, name).eq(SysDictData::getDictId,dictId));
        if (sysDictDataName != null && !sysDictDataName.getId().equals(id)) {
            throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "字典数据项名称不能重复");
        }
        SysDictData sysDictDataCode = sysDictDataMapper.selectOne(new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getValue, value).eq(SysDictData::getDictId,dictId));
        if (sysDictDataCode != null && !sysDictDataCode.getId().equals(id)) {
            throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "字典数据项值不能重复");
        }
    }

}

