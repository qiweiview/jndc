package com.view.controller;

import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.annotation.AdminPrefix;
import com.view.annotation.OperationLog;
import com.view.convert.sysConfig.SysConfigConvert;
import com.view.dao.entity.SysConfig;
import com.view.dto.sysConfig.SysConfigCreateDTO;
import com.view.dto.sysConfig.SysConfigQueryDTO;
import com.view.dto.sysConfig.SysConfigUpdateDTO;
import com.view.enums.BooleanEnum;
import com.view.enums.OperBusinessType;
import com.view.exception.ServiceException;
import com.view.model.vo.ResponseResult;
import com.view.service.SysConfigService;
import com.view.utils.BeanCopyUtils;
import com.view.utils.StringUtils;
import com.view.vo.sysConfig.ConfigSingleVO;
import com.view.vo.sysConfig.SysConfigSimpleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-02 14:46
 * @description: 系统配置
 */
@AdminPrefix
@RequestMapping("/sysConfig")
@RequiredArgsConstructor
public class SysConfigController {


    private final SysConfigService configService;

    /**
     * 系统配置列表
     *
     * @return 所有数据
     */
    @GetMapping("/list")
    @SaCheckPermission("system:config:query")
    public ResponseResult<IPage<SysConfigSimpleVO>> selectAll(SysConfigQueryDTO queryDTO) {
        LambdaQueryWrapper<SysConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(queryDTO.getConfigName()), SysConfig::getConfigName, queryDTO.getConfigName());
        Page<SysConfig> configPage = configService.page(new Page<SysConfig>(queryDTO.getCurrent(), queryDTO.getSize()), queryWrapper);
        Page<SysConfigSimpleVO> sysConfigSimpleVOPage = SysConfigConvert.INSTANCE.convertSimplePage(configPage);
        return ResponseResult.ok(sysConfigSimpleVOPage);
    }

    /**
     * 获得配置详情
     *
     * @return 单条数据
     */
    @PostMapping("/get")
    public ResponseResult<ConfigSingleVO> selectOne(@RequestBody SysConfigQueryDTO queryDTO) {
        Long id = queryDTO.getId();
        SysConfig fileConfig = this.configService.getById(id);
        if (fileConfig == null) {
            throw new ServiceException("配置不存在");
        }
        ConfigSingleVO configSingleVO = BeanCopyUtils.copyBean(fileConfig, ConfigSingleVO.class);
        return ResponseResult.ok(configSingleVO);
    }

    /**
     * 创建系统配置
     *
     * @param createDTO 实体对象
     * @return 新增结果
     */
    @OperationLog(title = "配置管理",businessType = OperBusinessType.INSERT)
    @PostMapping("/create")
    @SaCheckPermission("system:config:create")
    public ResponseResult<Integer> insert(@Valid @RequestBody SysConfigCreateDTO createDTO) {
        SysConfig config = SysConfigConvert.INSTANCE.convertCreateDTO(createDTO);
        return ResponseResult.ok(configService.createConfig(config));
    }

    /**
     * 修改系统配置
     *
     * @param updateDTO 实体对象
     * @return 修改结果
     */
    @OperationLog(title = "配置管理",businessType = OperBusinessType.UPDATE)
    @PutMapping("/update")
    @SaCheckPermission("system:config:update")
    public ResponseResult<Integer> update(@Valid @RequestBody SysConfigUpdateDTO updateDTO) {
        SysConfig config = SysConfigConvert.INSTANCE.convertUpdateDTO(updateDTO);
        return ResponseResult.ok(configService.updateConfig(config));
    }

    /**
     * 删除数据
     *
     * @return 删除结果
     */
    @OperationLog(title = "配置管理",businessType = OperBusinessType.DELETE)
    @DeleteMapping("/delete")
    @SaCheckPermission("system:config:delete")
    public ResponseResult<Boolean> delete(@RequestBody SysConfigQueryDTO queryDTO) {
        Long id = queryDTO.getId();
        SysConfig sysConfig = configService.getById(id);
        if (sysConfig.getType().equals(BooleanEnum.TRUE.getValue())) {
            throw new ServiceException("系统内置配置无法删除");
        }
        return ResponseResult.ok(configService.removeById(id));
    }

    /**
     * 缓存刷新
     *
     */
    @GetMapping("/refreshCache")
    @SaCheckPermission("system:config:refreshCache")
    public ResponseResult<Void> refreshCache() {
        configService.resetConfigCache();
        return ResponseResult.ok();
    }

    /**
     * 根据key获取配置
     *
     * @return 单条数据
     */
    @SaIgnore
    @PostMapping("/getByKey")
    public ResponseResult<ConfigSingleVO> selectByKey(@RequestBody SysConfigQueryDTO queryDTO) {
        String configKey = queryDTO.getConfigKey();
        if (configKey==null){
            throw new ServiceException("字典键不能为空");
        }
        String configValueByKey = configService.getConfigValueByKey(configKey);

        ConfigSingleVO configSingleVO = new ConfigSingleVO();
        configSingleVO.setConfigKey(configKey);
        configSingleVO.setConfigValue(configValueByKey);
        return ResponseResult.ok(configSingleVO);
    }


}
