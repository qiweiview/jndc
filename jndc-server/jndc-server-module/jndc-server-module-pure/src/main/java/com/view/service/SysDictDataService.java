package com.view.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.view.dao.entity.SysDictData;

/**
 * 字典数据表(SysDictData)表服务接口
 *
 * @author sjh
 * @since 2024-04-24 10:35:56
 */
public interface SysDictDataService extends IService<SysDictData> {

    /**
     * 创建
     * @param sysDictData 字典数据项信息
     * @return 字典ID
     */
    Long createDictData(SysDictData sysDictData);

    /**
     * 根据ID修改
     * @param sysDictData 字典数据项信息
     * @return 字典数据项ID
     */
    Long updateDictData(SysDictData sysDictData);

}

