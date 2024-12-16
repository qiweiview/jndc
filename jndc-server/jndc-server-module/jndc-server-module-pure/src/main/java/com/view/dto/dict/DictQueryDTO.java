package com.view.dto.dict;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.dao.entity.SysDict;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-06-26 21:13
 * @description: 字典查询对象
 */
@Data
public class DictQueryDTO extends Page<SysDict> {

    private String dictName;

    private String dictCode;

    private Long id;

    private Integer status;
}
