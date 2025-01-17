package com.view.jndc.manage.serviceI.jndc_server_app.Impl;

import com.view.jndc.manage.enums.server.JNDCServerAPPStatus;
import com.view.jndc.manage.enums.server.JNDCServerBindType;
import com.view.jndc.manage.model.jndc_server_app.JndcServerAppStructMapper;
import com.view.jndc.manage.dao.jndc_server_app.JndcServerAppDao;
import com.view.jndc.manage.model.jndc_server_app.vo.JndcServerAppVO;
import com.view.jndc.manage.model.jndc_server_app.d_o.JndcServerAppDO;
import com.view.jndc.manage.model.jndc_server_app.dto.JndcServerAppDTO;
// import com.view.jndc.manage.config.dynamic_datasource.DynamicDataSource;
import com.view.jndc.manage.serviceI.jndc_server_app.JndcServerAppServiceI;
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
public class JndcServerAppServiceImpl implements JndcServerAppServiceI {

    private final JndcServerAppDao jndcServerAppDao;

    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 分页查询
     */
    @Override
    public IPage queryPage(JndcServerAppDTO jndcServerAppDTO) {
        JndcServerAppDO copy = JndcServerAppStructMapper.INSTANCE.toDO(jndcServerAppDTO);
        Page page = new Page(jndcServerAppDTO.getCurrent(), jndcServerAppDTO.getSize());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        IPage<JndcServerAppDO> pageResult = jndcServerAppDao.listPage(page, copy);
        IPage<JndcServerAppVO> convert =
                pageResult.convert(x -> JndcServerAppStructMapper.INSTANCE.toVO(x));
        return convert;
    }

    /**
     * 查询
     */
    @Override
    public List<JndcServerAppVO> queryList(JndcServerAppDTO jndcServerAppDTO) {
        JndcServerAppDO copy = JndcServerAppStructMapper.INSTANCE.toDO(jndcServerAppDTO);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        List<JndcServerAppDO> list = jndcServerAppDao.list(copy);
        List<JndcServerAppVO> collect =
                list.stream()
                        .map(x -> JndcServerAppStructMapper.INSTANCE.toVO(x))
                        .collect(Collectors.toList());
        return collect;
    }

    /**
     * 保存
     */
    @Override
    public JndcServerAppDO save(JndcServerAppDTO jndcServerAppDTO) {
        JndcServerAppDO copy = JndcServerAppStructMapper.INSTANCE.toDO(jndcServerAppDTO);
        copy.setId(snowflakeIdWorker.nextId());
        copy.setCreateTime(LocalDateTime.now());
        copy.setBindStatus(JNDCServerAPPStatus.PAUSE.value);

        Integer bindPort = copy.getBindPort();
        List<JndcServerAppDO> jndcServerAppDOS = jndcServerAppDao.listByBindPort(bindPort);
        if (!jndcServerAppDOS.isEmpty()) {
            throw new BizException("端口" + bindPort + "重复");
        }

        String bindType = jndcServerAppDTO.getBindType();
        if (JNDCServerBindType.MOCK_SERVER.value.equals(bindType)) {
            //mock 服务
        }


        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcServerAppDao.insert(copy);
        return copy;
    }

    /**
     * 修改
     */
    @Override
    public void updateById(JndcServerAppDTO jndcServerAppDTO) {
        // 确认存在
        getById(jndcServerAppDTO.getId());

        JndcServerAppDO copy = JndcServerAppStructMapper.INSTANCE.toDO(jndcServerAppDTO);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcServerAppDao.updateById(copy);
    }

    /**
     * 删除
     */
    @Override
    public void removeById(Serializable id) {
        // 确认存在
        getById(id);

        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcServerAppDao.deleteById(id);
    }

    /**
     * 获取单条数据
     */
    @Override
    public JndcServerAppDTO getById(Serializable id) {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        JndcServerAppDO JndcServerAppDO = jndcServerAppDao.selectById(id);
        if (JndcServerAppDO == null) {
            throw new BizException("数据不存在");
        }
        return JndcServerAppStructMapper.INSTANCE.toDTO(JndcServerAppDO);
    }

    @Override
    public void updateStatusByServiceId(String serviceId, String value) {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        jndcServerAppDao.updateStatusByServiceId(serviceId, value);
    }

    @Override
    public JndcServerAppDTO getByServiceId(String serviceId) {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        JndcServerAppDO JndcServerAppDO = jndcServerAppDao.getByServiceId(serviceId);
        if (JndcServerAppDO == null) {
            return null;
        }
        return JndcServerAppStructMapper.INSTANCE.toDTO(JndcServerAppDO);
    }
}
