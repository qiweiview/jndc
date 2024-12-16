package com.view.dto.dictData;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.dao.entity.SysDictData;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-06-29 14:55
 * @description: 字典数据项查询
 */
@Data
public class DictDataQueryDTO extends Page<SysDictData> {

    @NotNull(message = "字典ID不能为空")
    private Long dictId;

    private Long id;

    private String name;

    private String value;

    private Integer status;
}
