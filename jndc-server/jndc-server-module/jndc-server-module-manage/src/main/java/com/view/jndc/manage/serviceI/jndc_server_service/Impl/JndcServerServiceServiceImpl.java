package com.view.jndc.manage.serviceI.jndc_server_service.Impl;

import com.view.jndc.manage.model.jndc_server_service.JndcServerServiceStructMapper;
import com.view.jndc.manage.dao.jndc_server_service.JndcServerServiceDao;
import com.view.jndc.manage.model.jndc_server_service.vo.JndcServerServiceVO;
import com.view.jndc.manage.model.jndc_server_service.d_o.JndcServerServiceDO;
import com.view.jndc.manage.model.jndc_server_service.dto.JndcServerServiceDTO;
// import com.view.jndc.manage.config.dynamic_datasource.DynamicDataSource;
import com.view.jndc.manage.serviceI.jndc_server_service.JndcServerServiceServiceI;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
// import com.view.free.common.base.exception.BizException;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.free_lite.common.config.exception.BizException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.io.Serializable;

import com.view.free_lite.common.utils.SnowflakeIdWorker;

@Service
@RequiredArgsConstructor
public class JndcServerServiceServiceImpl implements JndcServerServiceServiceI {

    private final JndcServerServiceDao jndcServerServiceDao;

    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 分页查询
     */
    @Override
    public IPage queryPage(JndcServerServiceDTO jndcServerServiceDTO) {
        JndcServerServiceDO copy = JndcServerServiceStructMapper.INSTANCE.toDO(jndcServerServiceDTO);
        Page page = new Page(jndcServerServiceDTO.getCurrent(), jndcServerServiceDTO.getSize());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        IPage<JndcServerServiceDO> pageResult = jndcServerServiceDao.listPage(page, copy);
        IPage<JndcServerServiceVO> convert =
                pageResult.convert(x -> JndcServerServiceStructMapper.INSTANCE.toVO(x));
        return convert;
    }

    /**
     * 查询
     */
    @Override
    public List<JndcServerServiceVO> queryList(JndcServerServiceDTO jndcServerServiceDTO) {
        JndcServerServiceDO copy = JndcServerServiceStructMapper.INSTANCE.toDO(jndcServerServiceDTO);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        List<JndcServerServiceDO> list = jndcServerServiceDao.list(copy);
        List<JndcServerServiceVO> collect =
                list.stream()
                        .map(x -> JndcServerServiceStructMapper.INSTANCE.toVO(x))
                        .collect(Collectors.toList());
        return collect;
    }

    /**
     * 保存
     */
    @Override
    public JndcServerServiceDO save(JndcServerServiceDTO jndcServerServiceDTO) {
        JndcServerServiceDO copy = JndcServerServiceStructMapper.INSTANCE.toDO(jndcServerServiceDTO);
        copy.setId(snowflakeIdWorker.nextId());
        copy.setCreateTime(LocalDateTime.now());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcServerServiceDao.insert(copy);
        return copy;
    }

    /**
     * 修改
     */
    @Override
    public void updateById(JndcServerServiceDTO jndcServerServiceDTO) {
        // 确认存在
        getById(jndcServerServiceDTO.getId());

        JndcServerServiceDO copy = JndcServerServiceStructMapper.INSTANCE.toDO(jndcServerServiceDTO);
        copy.setUpdateTime(LocalDateTime.now());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcServerServiceDao.updateById(copy);
    }

    /**
     * 删除
     */
    @Override
    public void removeById(Serializable id) {
        // 确认存在
        getById(id);

        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcServerServiceDao.deleteById(id);
    }

    /**
     * 获取单条数据
     */
    @Override
    public JndcServerServiceDTO getById(Serializable id) {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        JndcServerServiceDO JndcServerServiceDO = jndcServerServiceDao.selectById(id);
        if (JndcServerServiceDO == null) {
            throw new BizException("数据不存在");
        }
        return JndcServerServiceStructMapper.INSTANCE.toDTO(JndcServerServiceDO);
    }
}
