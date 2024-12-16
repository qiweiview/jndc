package com.view.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.view.dao.entity.SysDict;
import com.view.vo.dict.DictAndDataVO;

import java.util.List;

/**
 * 字典表(SysDict)表服务接口
 *
 * @author sjh
 * @since 2024-04-24 10:35:54
 */
public interface SysDictService extends IService<SysDict> {

    /**
     * 创建
     * @param sysDict 字典信息
     * @return 字典ID
     */
    Long createDict(SysDict sysDict);

    /**
     * 根据ID修改
     * @param sysDict 字典信息
     * @return 字典ID
     */
    Long updateDict(SysDict sysDict);

    List<DictAndDataVO> listAllDictAndData();
}

