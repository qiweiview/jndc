package com.view.jndc.manage.serviceI.jndc_server_app.Impl;

import com.view.jndc.manage.component.server.ServerAppHolder;
import com.view.jndc.manage.enums.server.JNDCServerAPPStatus;
import com.view.jndc.manage.enums.server.JNDCServerBindType;
import com.view.jndc.manage.enums.server.JNDCServerStatusEnum;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import com.view.jndc.manage.model.jndc_server_app.JndcServerAppStructMapper;
import com.view.jndc.manage.dao.jndc_server_app.JndcServerAppDao;
import com.view.jndc.manage.model.jndc_server_app.vo.JndcServerAppVO;
import com.view.jndc.manage.model.jndc_server_app.d_o.JndcServerAppDO;
import com.view.jndc.manage.model.jndc_server_app.dto.JndcServerAppDTO;
// import com.view.jndc.manage.config.dynamic_datasource.DynamicDataSource;
import com.view.jndc.manage.serviceI.jndc_server_app.JndcServerAppServiceI;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class JndcServerAppServiceImpl implements JndcServerAppServiceI {

    private final JndcServerAppDao jndcServerAppDao;

    private final SnowflakeIdWorker snowflakeIdWorker;

    private final ServerAppHolder serverAppHolder;

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
        JndcServerAppDTO dbData = getById(jndcServerAppDTO.getId());


        JndcServerAppDO copy = JndcServerAppStructMapper.INSTANCE.toDO(jndcServerAppDTO);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcServerAppDao.updateById(copy);

        String bindStatus = jndcServerAppDTO.getBindStatus();
        String bindStatusDB = dbData.getBindStatus();

        if (!bindStatusDB.equals(bindStatus)) {
            //todo 有状态变化才操作
            if (JNDCServerAPPStatus.LISTEN.value.equals(bindStatus)) {
                jndcServerAppDao.updateStatus(copy.getId(), JNDCServerAPPStatus.PROCESSING.value);
                serverAppHolder.startServer(dbData);
            } else if (JNDCServerAPPStatus.PAUSE.value.equals(bindStatus)) {
                // todo 停止服务
                serverAppHolder.stopServer(dbData);
            }
        }
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

    @Override
    public void pauseOperation(Long id) {
        JndcServerAppDTO byId = getById(id);
        if (!JNDCServerAPPStatus.LISTEN.value.equals(byId.getBindStatus())) {
            throw new BizException("服务器状态必须为监听");
        }
        byId.setBindStatus(JNDCServerAPPStatus.PAUSE.value);
        //复用链路，存在重复查询，频率较低，不做优化
        updateById(byId);
    }

    @Override
    public void listenOperation(Long id) {
        JndcServerAppDTO byId = getById(id);

        if (!JNDCServerAPPStatus.PAUSE.value.equals(byId.getBindStatus())) {
            throw new BizException("服务器状态必须为暂停");
        }
        byId.setBindStatus(JNDCServerAPPStatus.LISTEN.value);
        //复用链路，存在重复查询，频率较低，不做优化
        updateById(byId);
    }

    @Override
    public void resetAllServer() {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        int i = jndcServerAppDao.resetAllServerStatus();

        log.info("重置app服务状态数量:{}", i);
    }
}
