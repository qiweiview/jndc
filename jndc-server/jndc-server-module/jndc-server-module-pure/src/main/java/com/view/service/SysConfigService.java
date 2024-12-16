package com.view.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.view.dao.entity.SysConfig;

/**
 * 参数配置表(SysConfig)表服务接口
 *
 * @author sjh
 * @since 2024-08-02 14:22:24
 */
public interface SysConfigService extends IService<SysConfig> {

    /**
     * 创建
     */
    Integer createConfig(SysConfig config);

    /**
     * 修改
     */
    Integer updateConfig(SysConfig config);

    /**
     * 加载参数缓存数据
     */
    void loadingConfigCache();

    /**
     * 清空参数缓存数据
     */
    void clearConfigCache();

    /**
     * 重置参数缓存数据
     */
    void resetConfigCache();

    /**
     * 获取配置值
     */
    String getConfigValueByKey(String key);

}

