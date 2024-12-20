package com.view.jndc.manage.serviceI.jndc_log.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.free_lite.common.config.exception.BizException;
import com.view.free_lite.common.utils.SnowflakeIdWorker;
import com.view.jndc.manage.dao.jndc_log.JndcLogDao;
import com.view.jndc.manage.model.jndc_log.JndcLogStructMapper;
import com.view.jndc.manage.model.jndc_log.d_o.JndcLogDO;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.model.jndc_log.vo.JndcLogVO;
import com.view.jndc.manage.serviceI.jndc_log.JndcLogServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JndcLogServiceImpl implements JndcLogServiceI {

    private final JndcLogDao jndcLogDao;

    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 分页查询
     */
    @Override
    public IPage queryPage(JndcLogDTO jndcLogDTO) {
        JndcLogDO copy = JndcLogStructMapper.INSTANCE.toDO(jndcLogDTO);
        Page page = new Page(jndcLogDTO.getCurrent(), jndcLogDTO.getSize());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        IPage<JndcLogDO> pageResult = jndcLogDao.listPage(page, copy);
        IPage<JndcLogVO> convert = pageResult.convert(x -> JndcLogStructMapper.INSTANCE.toVO(x));
        return convert;
    }

    /**
     * 查询
     */
    @Override
    public List<JndcLogVO> queryList(JndcLogDTO jndcLogDTO) {
        JndcLogDO copy = JndcLogStructMapper.INSTANCE.toDO(jndcLogDTO);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        List<JndcLogDO> list = jndcLogDao.list(copy);
        List<JndcLogVO> collect =
                list.stream().map(x -> JndcLogStructMapper.INSTANCE.toVO(x)).collect(Collectors.toList());
        return collect;
    }

    /**
     * 保存
     */
    @Override
    public JndcLogDO save(JndcLogDTO jndcLogDTO) {
        JndcLogDO copy = JndcLogStructMapper.INSTANCE.toDO(jndcLogDTO);
        copy.setId(snowflakeIdWorker.nextId());
        copy.setLogTime(LocalDateTime.now());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcLogDao.insert(copy);
        return copy;
    }

    /**
     * 修改
     */
    @Override
    public void updateById(JndcLogDTO jndcLogDTO) {
        // 确认存在
        getById(jndcLogDTO.getId());

        JndcLogDO copy = JndcLogStructMapper.INSTANCE.toDO(jndcLogDTO);
        copy.setLogTime(LocalDateTime.now());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcLogDao.updateById(copy);
    }

    /**
     * 删除
     */
    @Override
    public void removeById(Serializable id) {
        // 确认存在
        getById(id);

        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcLogDao.deleteById(id);
    }

    /**
     * 获取单条数据
     */
    @Override
    public JndcLogDTO getById(Serializable id) {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        JndcLogDO JndcLogDO = jndcLogDao.selectById(id);
        if (JndcLogDO == null) {
            throw new BizException("数据不存在");
        }
        return JndcLogStructMapper.INSTANCE.toDTO(JndcLogDO);
    }

    @Override
    public void deleteBatch(List<Long> idList) {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        jndcLogDao.deleteBatchIds(idList);
    }
}
