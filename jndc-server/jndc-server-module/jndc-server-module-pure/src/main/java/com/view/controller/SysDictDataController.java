package com.view.controller;

import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.annotation.AdminPrefix;
import com.view.annotation.OperationLog;
import com.view.dao.entity.SysDictData;
import com.view.dto.dictData.DictDataQueryDTO;
import com.view.dto.dictData.SysDictDataCreateDTO;
import com.view.dto.dictData.SysDictDataUpdateDTO;
import com.view.enums.OperBusinessType;
import com.view.model.vo.ResponseResult;
import com.view.service.SysDictDataService;
import com.view.utils.BeanCopyUtils;
import com.view.utils.StringUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;


@AdminPrefix
@RequestMapping("/sysDictData")
@RequiredArgsConstructor
public class SysDictDataController  {
    /**
     * 服务对象
     */
    private final SysDictDataService sysDictDataService;

    /**
     * 分页查询字典数据项
     *
     * @param queryDTO 查询实体
     * @return 所有数据
     */
    @GetMapping("/list")
    @SaCheckPermission("system:dict:data:query")
    public ResponseResult<Page<SysDictData>> selectAll(@Valid DictDataQueryDTO queryDTO) {
        LambdaQueryWrapper<SysDictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDictData::getDictId,queryDTO.getDictId()).like(StringUtils.isNotEmpty(queryDTO.getName()),SysDictData::getName,queryDTO.getName())
                .like(StringUtils.isNotEmpty(queryDTO.getValue()),SysDictData::getValue,queryDTO.getValue())
                .eq(Objects.nonNull(queryDTO.getStatus()),SysDictData::getStatus,queryDTO.getStatus()).orderByAsc(SysDictData::getSortOrder);
        return ResponseResult.ok(this.sysDictDataService.page(queryDTO,queryWrapper));
    }

    /**
     * 通过字典主键查询数据项
     *
     * @return 单条数据
     */
    @GetMapping("/get")
    public ResponseResult<SysDictData> selectOne(@RequestBody DictDataQueryDTO queryDTO) {
        Long id=queryDTO.getId();
        return ResponseResult.ok(this.sysDictDataService.getById(id));
    }

    /**
     * 新增字典数据项
     *
     * @param createDTO 实体对象
     * @return 新增结果
     */
    @OperationLog(title = "字典数据项管理",businessType = OperBusinessType.INSERT)
    @PostMapping("/create")
    @SaCheckPermission("system:dict:data:create")
    public ResponseResult<Long> insert(@Valid @RequestBody SysDictDataCreateDTO createDTO) {
        SysDictData sysDictData = BeanCopyUtils.copyBean(createDTO, SysDictData.class);
        return ResponseResult.ok(this.sysDictDataService.createDictData(sysDictData));
    }

    /**
     * 修改字典数据项
     *
     * @param updateDTO 实体对象
     * @return 修改结果
     */
    @OperationLog(title = "字典数据项管理",businessType = OperBusinessType.UPDATE)
    @PutMapping("update")
    @SaCheckPermission("system:dict:data:update")
    public ResponseResult<Long> update(@Valid @RequestBody SysDictDataUpdateDTO updateDTO) {
        SysDictData sysDictData = BeanCopyUtils.copyBean(updateDTO, SysDictData.class);
        return ResponseResult.ok(this.sysDictDataService.updateDictData(sysDictData));
    }

    /**
     * 删除字典数据项
     *
     * @param idList 主键结合
     * @return 删除结果
     */
    @OperationLog(title = "字典数据项管理",businessType = OperBusinessType.DELETE)
    @DeleteMapping("/delete")
    @SaCheckPermission("system:dict:data:delete")
    public ResponseResult<Boolean> delete(@RequestBody List<Long> idList) {
        return ResponseResult.ok(this.sysDictDataService.removeByIds(idList));
    }
}

