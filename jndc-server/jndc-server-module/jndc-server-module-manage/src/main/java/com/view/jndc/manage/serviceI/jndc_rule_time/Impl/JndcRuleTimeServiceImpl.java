package com.view.jndc.manage.serviceI.jndc_rule_time.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.free_lite.common.config.exception.BizException;
import com.view.free_lite.common.utils.SnowflakeIdWorker;
import com.view.jndc.manage.dao.jndc_rule_time.JndcRuleTimeDao;
import com.view.jndc.manage.model.jndc_rule_time.JndcRuleTimeStructMapper;
import com.view.jndc.manage.model.jndc_rule_time.d_o.JndcRuleTimeDO;
import com.view.jndc.manage.model.jndc_rule_time.dto.JndcRuleTimeDTO;
import com.view.jndc.manage.model.jndc_rule_time.vo.JndcRuleTimeVO;
import com.view.jndc.manage.serviceI.jndc_rule_time.JndcRuleTimeServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JndcRuleTimeServiceImpl implements JndcRuleTimeServiceI {

    private final JndcRuleTimeDao jndcRuleTimeDao;

    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 分页查询
     */
    @Override
    public IPage queryPage(JndcRuleTimeDTO jndcRuleTimeDTO) {
        JndcRuleTimeDO copy = JndcRuleTimeStructMapper.INSTANCE.toDO(jndcRuleTimeDTO);
        Page page = new Page(jndcRuleTimeDTO.getCurrent(), jndcRuleTimeDTO.getSize());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        IPage<JndcRuleTimeDO> pageResult = jndcRuleTimeDao.listPage(page, copy);
        IPage<JndcRuleTimeVO> convert =
                pageResult.convert(x -> JndcRuleTimeStructMapper.INSTANCE.toVO(x));
        return convert;
    }

    /**
     * 查询
     */
    @Override
    public List<JndcRuleTimeVO> queryList(JndcRuleTimeDTO jndcRuleTimeDTO) {
        JndcRuleTimeDO copy = JndcRuleTimeStructMapper.INSTANCE.toDO(jndcRuleTimeDTO);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        List<JndcRuleTimeDO> list = jndcRuleTimeDao.list(copy);
        List<JndcRuleTimeVO> collect =
                list.stream()
                        .map(x -> JndcRuleTimeStructMapper.INSTANCE.toVO(x))
                        .collect(Collectors.toList());
        return collect;
    }

    /**
     * 保存
     */
    @Override
    public JndcRuleTimeDO save(JndcRuleTimeDTO jndcRuleTimeDTO) {
        JndcRuleTimeDO copy = JndcRuleTimeStructMapper.INSTANCE.toDO(jndcRuleTimeDTO);
        copy.setId(snowflakeIdWorker.nextId());
        copy.setCreateTime(LocalDateTime.now());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcRuleTimeDao.insert(copy);
        return copy;
    }

    /**
     * 修改
     */
    @Override
    public void updateById(JndcRuleTimeDTO jndcRuleTimeDTO) {
        // 确认存在
        getById(jndcRuleTimeDTO.getId());

        JndcRuleTimeDO copy = JndcRuleTimeStructMapper.INSTANCE.toDO(jndcRuleTimeDTO);
        copy.setUpdateTime(LocalDateTime.now());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcRuleTimeDao.updateById(copy);
    }

    /**
     * 删除
     */
    @Override
    public void removeById(Serializable id) {
        // 确认存在
        getById(id);

        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcRuleTimeDao.deleteById(id);
    }

    /**
     * 获取单条数据
     */
    @Override
    public JndcRuleTimeDTO getById(Serializable id) {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        JndcRuleTimeDO JndcRuleTimeDO = jndcRuleTimeDao.selectById(id);
        if (JndcRuleTimeDO == null) {
            throw new BizException("数据不存在");
        }
        return JndcRuleTimeStructMapper.INSTANCE.toDTO(JndcRuleTimeDO);
    }
}
