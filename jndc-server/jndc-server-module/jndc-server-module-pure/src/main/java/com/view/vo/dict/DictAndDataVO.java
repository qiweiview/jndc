package com.view.vo.dict;

import lombok.Data;

import java.util.List;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-09-03 10:35
 * @description: 字典以及字典数据
 */
@Data
public class DictAndDataVO {

    private String dictCode;

    private List<DictDataVO> dictValue;
}
