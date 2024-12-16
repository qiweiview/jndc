package com.view.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.view.constant.Constants;
import com.view.dao.entity.SysConfig;
import com.view.dao.mapper.SysConfigMapper;
import com.view.enums.BooleanEnum;
import com.view.enums.StatusCodeEnum;
import com.view.exception.ServiceException;
import com.view.service.SysConfigService;
import com.view.text.Convert;
import com.view.utils.RedisCacheUtils;
import com.view.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.view.constant.Constants.SYSTEM_CONFIG_KEY;

/**
 * 参数配置表(SysConfig)表服务实现类
 *
 * @author sjh
 * @since 2024-08-02 14:22:26
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    private final RedisCacheUtils redisCacheUtils;

    private final SysConfigMapper configMapper;

    @Override
    public Integer createConfig(SysConfig config) {
        validateKeyUniqueness(null, config.getConfigKey());
        config.setCreateBy(Long.valueOf(StpUtil.getLoginId().toString()));
        config.setType(BooleanEnum.FALSE.getValue());
        int row = configMapper.insert(config);
        if (row > 0) {
            redisCacheUtils.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    @Override
    public Integer updateConfig(SysConfig config) {
        SysConfig temp = configMapper.selectById(config.getId());
        if (Objects.isNull(temp)) {
            throw new ServiceException("配置不存在");
        }
        config.setUpdateBy(Long.valueOf(StpUtil.getLoginId().toString()));
        if (!StringUtils.equals(temp.getConfigKey(), config.getConfigKey())) {
            redisCacheUtils.deleteObject(getCacheKey(temp.getConfigKey()));
        }
        int row = configMapper.updateById(config);
        if (row > 0) {
            redisCacheUtils.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        return row;
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache() {
        List<SysConfig> configsList = configMapper.selectList(null);
        for (SysConfig config : configsList) {
            redisCacheUtils.setCacheObject(getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache() {
        Collection<String> keys = redisCacheUtils.keys(Constants.SYSTEM_CONFIG_KEY + "*");
        redisCacheUtils.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache() {
        clearConfigCache();
        loadingConfigCache();
    }

    @Override
    public String getConfigValueByKey(String key) {
        String configValue = Convert.toStr(redisCacheUtils.getCacheObject(getCacheKey(key), String.class));
        if (StringUtils.isNotEmpty(configValue)) {
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(key);
        SysConfig retConfig = configMapper.selectOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, key));
        if (StringUtils.isNotNull(retConfig)) {
            redisCacheUtils.setCacheObject(getCacheKey(key), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    private String getCacheKey(String configKey) {
        return SYSTEM_CONFIG_KEY + configKey;
    }

    private void validateKeyUniqueness(Integer id, String configKey) {
        SysConfig sysConfig = this.getOne(new LambdaQueryWrapper<SysConfig>().eq(SysConfig::getConfigKey, configKey));
        if (sysConfig != null && !sysConfig.getId().equals(id)) {
            throw new ServiceException(StatusCodeEnum.VALID_ERROR.getCode(), "配置键名已存在");
        }
    }
}

