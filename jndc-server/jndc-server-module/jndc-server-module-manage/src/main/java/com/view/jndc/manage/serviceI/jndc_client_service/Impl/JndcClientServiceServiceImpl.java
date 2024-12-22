package com.view.jndc.manage.serviceI.jndc_client_service.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.free_lite.common.config.exception.BizException;
import com.view.free_lite.common.utils.SnowflakeIdWorker;
import com.view.free_lite.common.utils.UniqueId;
import com.view.jndc.manage.dao.jndc_client_service.JndcClientServiceDao;
import com.view.jndc.manage.enums.JNDCClientServiceStatusEnum;
import com.view.jndc.manage.model.jndc_client_service.JndcClientServiceStructMapper;
import com.view.jndc.manage.model.jndc_client_service.d_o.JndcClientServiceDO;
import com.view.jndc.manage.model.jndc_client_service.dto.JndcClientServiceDTO;
import com.view.jndc.manage.model.jndc_client_service.vo.JndcClientServiceVO;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.serviceI.jndc_client_service.JndcClientServiceServiceI;
import com.view.jndc.manage.serviceI.jndc_log.JndcLogServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JndcClientServiceServiceImpl implements JndcClientServiceServiceI {

    private final JndcClientServiceDao jndcClientServiceDao;

    private final JndcLogServiceI logServiceI;

    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 分页查询
     */
    @Override
    public IPage queryPage(JndcClientServiceDTO jndcClientServiceDTO) {
        JndcClientServiceDO copy = JndcClientServiceStructMapper.INSTANCE.toDO(jndcClientServiceDTO);
        Page page = new Page(jndcClientServiceDTO.getCurrent(), jndcClientServiceDTO.getSize());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        IPage<JndcClientServiceDO> pageResult = jndcClientServiceDao.listPage(page, copy);
        IPage<JndcClientServiceVO> convert =
                pageResult.convert(x -> JndcClientServiceStructMapper.INSTANCE.toVO(x));
        return convert;
    }

    /**
     * 查询
     */
    @Override
    public List<JndcClientServiceVO> queryList(JndcClientServiceDTO jndcClientServiceDTO) {
        JndcClientServiceDO copy = JndcClientServiceStructMapper.INSTANCE.toDO(jndcClientServiceDTO);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        List<JndcClientServiceDO> list = jndcClientServiceDao.list(copy);
        List<JndcClientServiceVO> collect =
                list.stream()
                        .map(x -> JndcClientServiceStructMapper.INSTANCE.toVO(x))
                        .collect(Collectors.toList());
        return collect;
    }

    /**
     * 保存
     */
    @Override
    public JndcClientServiceDO save(JndcClientServiceDTO jndcClientServiceDTO) {
        JndcClientServiceDO copy = JndcClientServiceStructMapper.INSTANCE.toDO(jndcClientServiceDTO);
        copy.setId(snowflakeIdWorker.nextId());
        copy.setCreateTime(LocalDateTime.now());
        copy.setServiceUniqueId(UniqueId.generate());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcClientServiceDao.insert(copy);

        JndcLogDTO jndcLogDTO = new JndcLogDTO();
        jndcLogDTO.setLogContent("创建服务：" + copy.getServiceName());
        jndcLogDTO.setLogTime(LocalDateTime.now());
        jndcLogDTO.setLogType("client");
        jndcLogDTO.setSourceId(jndcClientServiceDTO.getClientId());

        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        logServiceI.save(jndcLogDTO);
        return copy;
    }

    /**
     * 修改
     */
    @Override
    public void updateById(JndcClientServiceDTO jndcClientServiceDTO) {
        // 确认存在
        getById(jndcClientServiceDTO.getId());

        JndcClientServiceDO copy = JndcClientServiceStructMapper.INSTANCE.toDO(jndcClientServiceDTO);
        copy.setUpdateTime(LocalDateTime.now());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcClientServiceDao.updateById(copy);
    }

    /**
     * 删除
     */
    @Override
    public void removeById(Serializable id) {
        // 确认存在
        JndcClientServiceDTO byId = getById(id);

        if (JNDCClientServiceStatusEnum.REGISTER.value.equals(byId.getServiceStatus())) {
            throw new BizException("服务已注册，请先取消注册");
        }

        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcClientServiceDao.deleteById(id);

        JndcLogDTO jndcLogDTO = new JndcLogDTO();
        jndcLogDTO.setLogContent("删除服务：" + byId.getServiceName());
        jndcLogDTO.setLogTime(LocalDateTime.now());
        jndcLogDTO.setLogType("client");
        jndcLogDTO.setSourceId(byId.getClientId());

        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        logServiceI.save(jndcLogDTO);
    }

    /**
     * 获取单条数据
     */
    @Override
    public JndcClientServiceDTO getById(Serializable id) {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        JndcClientServiceDO JndcClientServiceDO = jndcClientServiceDao.selectById(id);
        if (JndcClientServiceDO == null) {
            throw new BizException("数据不存在");
        }
        return JndcClientServiceStructMapper.INSTANCE.toDTO(JndcClientServiceDO);
    }
}
