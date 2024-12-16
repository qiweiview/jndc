package com.view.controller;

import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.annotation.AdminPrefix;
import com.view.annotation.OperationLog;
import com.view.dao.entity.SysDict;
import com.view.dao.entity.SysDictData;
import com.view.dto.dict.DictQueryDTO;
import com.view.dto.dict.SysDictCreateDTO;
import com.view.dto.dict.SysDictUpdateDTO;
import com.view.enums.OperBusinessType;
import com.view.exception.ServiceException;
import com.view.model.vo.ResponseResult;
import com.view.service.SysDictDataService;
import com.view.service.SysDictService;
import com.view.utils.BeanCopyUtils;
import com.view.utils.StringUtils;
import com.view.vo.dict.DictAndDataVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;

import static com.view.enums.StatusCodeEnum.FAIL;


@AdminPrefix
@RequestMapping("/sysDict")
@RequiredArgsConstructor
public class SysDictController  {
    /**
     * 服务对象
     */
    private final SysDictService sysDictService;

    private final SysDictDataService sysDictDataService;

    /**
     * 查询字典列表数据
     *
     * @param queryDTO 查询实体
     * @return 所有数据
     */
    @GetMapping("/list")
    @SaCheckPermission("system:dict:query")
    public ResponseResult<Page<SysDict>> selectAll(DictQueryDTO queryDTO) {
        LambdaQueryWrapper<SysDict> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryDTO.getDictName()),SysDict::getDictName,queryDTO.getDictName())
                .like(StringUtils.isNotEmpty(queryDTO.getDictCode()),SysDict::getDictCode,queryDTO.getDictCode())
                .eq(Objects.nonNull(queryDTO.getStatus()),SysDict::getStatus,queryDTO.getStatus());
        return ResponseResult.ok(this.sysDictService.page(queryDTO,queryWrapper));
    }

    /**
     * 字典详情
     *
     * @return 单条数据
     */
    @GetMapping("/get")
    public ResponseResult<SysDict> selectOne(@RequestBody DictQueryDTO queryDTO) {
        Long id = queryDTO.getId();
        return ResponseResult.ok(this.sysDictService.getById(id));
    }

    /**
     * 新增字典
     *
     * @param createDTO 实体对象
     * @return 新增结果
     */
    @OperationLog(title = "字典管理",businessType = OperBusinessType.INSERT)
    @PostMapping("/create")
    @SaCheckPermission("system:dict:create")
    public ResponseResult<Long> insert(@Valid @RequestBody SysDictCreateDTO createDTO) {
        SysDict sysDict = BeanCopyUtils.copyBean(createDTO, SysDict.class);
        return ResponseResult.ok(this.sysDictService.createDict(sysDict));
    }

    /**
     * 修改字典
     *
     * @param updateDTO 实体对象
     * @return 修改结果
     */
    @OperationLog(title = "字典管理",businessType = OperBusinessType.UPDATE)
    @PutMapping("/update")
    @SaCheckPermission("system:dict:update")
    public ResponseResult<Long> update(@Valid @RequestBody SysDictUpdateDTO updateDTO) {
        SysDict sysDict = BeanCopyUtils.copyBean(updateDTO, SysDict.class);
        return ResponseResult.ok(this.sysDictService.updateDict(sysDict));
    }

    /**
     * 删除字典
     *
     * @return 删除结果
     */
    @OperationLog(title = "字典管理",businessType = OperBusinessType.DELETE)
    @DeleteMapping("/delete")
    @SaCheckPermission("system:dict:delete")
    public ResponseResult<Boolean> delete(@RequestBody DictQueryDTO queryDTO) {
        Long id = queryDTO.getId();
        long count = sysDictDataService.count(new LambdaQueryWrapper<SysDictData>().eq(SysDictData::getDictId, id));
        if (count>0){
            throw new ServiceException(FAIL.getCode(),"该字典下存在数据项，无法删除");
        }
        return ResponseResult.ok(this.sysDictService.removeById(id));
    }

    @GetMapping("/getAllDictAndData")
    public ResponseResult<List<DictAndDataVO>> getAllDictAndData(){
        return ResponseResult.ok(sysDictService.listAllDictAndData());
    }
}

