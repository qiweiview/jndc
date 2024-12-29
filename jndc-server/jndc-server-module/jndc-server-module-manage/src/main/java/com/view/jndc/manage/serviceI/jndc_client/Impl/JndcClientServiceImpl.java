package com.view.jndc.manage.serviceI.jndc_client.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.free_lite.common.config.exception.BizException;
import com.view.free_lite.common.utils.SnowflakeIdWorker;
import com.view.free_lite.common.utils.UniqueId;
import com.view.jndc.manage.component.client.JNDCClientHolder;
import com.view.jndc.manage.dao.jndc_client.JndcClientDao;
import com.view.jndc.manage.dao.jndc_client_service.JndcClientServiceDao;
import com.view.jndc.manage.enums.JNDCClientStatusEnum;
import com.view.jndc.manage.model.jndc_client.JndcClientStructMapper;
import com.view.jndc.manage.model.jndc_client.d_o.JndcClientDO;
import com.view.jndc.manage.model.jndc_client.dto.JndcClientDTO;
import com.view.jndc.manage.model.jndc_client.vo.JndcClientVO;
import com.view.jndc.manage.model.jndc_client_service.d_o.JndcClientServiceDO;
import com.view.jndc.manage.serviceI.jndc_client.JndcClientServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JndcClientServiceImpl implements JndcClientServiceI {

    private final JNDCClientHolder jndcClientHolder;

    private final JndcClientDao jndcClientDao;

    private final JndcClientServiceDao jndcClientServiceDao;

    private final SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 分页查询
     */
    @Override
    public IPage queryPage(JndcClientDTO jndcClientDTO) {
        JndcClientDO copy = JndcClientStructMapper.INSTANCE.toDO(jndcClientDTO);
        Page page = new Page(jndcClientDTO.getCurrent(), jndcClientDTO.getSize());
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        IPage<JndcClientDO> pageResult = jndcClientDao.listPage(page, copy);
        IPage<JndcClientVO> convert = pageResult.convert(x -> JndcClientStructMapper.INSTANCE.toVO(x));
        return convert;
    }

    /**
     * 查询
     */
    @Override
    public List<JndcClientVO> queryList(JndcClientDTO jndcClientDTO) {
        JndcClientDO copy = JndcClientStructMapper.INSTANCE.toDO(jndcClientDTO);
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        List<JndcClientDO> list = jndcClientDao.list(copy);
        List<JndcClientVO> collect =
                list.stream()
                        .map(x -> JndcClientStructMapper.INSTANCE.toVO(x))
                        .collect(Collectors.toList());
        return collect;
    }

    /**
     * 保存
     */
    @Override
    public JndcClientDO save(JndcClientDTO jndcClientDTO) {
        JndcClientDO copy = JndcClientStructMapper.INSTANCE.toDO(jndcClientDTO);
        copy.setId(snowflakeIdWorker.nextId());
        copy.setCreateTime(LocalDateTime.now());
        String generate = UniqueId.generate();
        copy.setUniqueId(generate);


        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcClientDao.insert(copy);

        String clientStatus = copy.getClientStatus();
        if (JNDCClientStatusEnum.CONNECT.value.equals(clientStatus)) {
            //查询所有服务
            List<JndcClientServiceDO> jndcClientServiceDOS = jndcClientServiceDao.listByClientId(copy.getId());
            jndcClientDTO.setClientServices(jndcClientServiceDOS);

            jndcClientDTO.setId(copy.getId());
            jndcClientDTO.setUniqueId(generate);
            jndcClientHolder.startClient(jndcClientDTO);
        }


        return copy;
    }

    /**
     * 修改
     */
    @Override
    public void updateById(JndcClientDTO jndcClientDTO) {
        // 确认存在
        JndcClientDTO dbData = getById(jndcClientDTO.getId());
        String clientStatusDB = dbData.getClientStatus();
        String clientStatus = jndcClientDTO.getClientStatus();

        JndcClientDO copy = JndcClientStructMapper.INSTANCE.toDO(jndcClientDTO);
        copy.setUpdateTime(LocalDateTime.now());

        if (JNDCClientStatusEnum.PROCESSING.value.equals(clientStatus)) {
            // todo 处理中
            //不更新
            copy.setClientStatus(null);
        }


        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcClientDao.updateById(copy);

        if (!clientStatusDB.equals(clientStatus)) {
            //todo 有状态变化才操作
            if (JNDCClientStatusEnum.CONNECT.value.equals(clientStatus)) {
                if (JNDCClientStatusEnum.PROCESSING.value.equals(clientStatusDB)) {
                    // todo 处理中
                    throw new BizException("请先停止客户端后再继续操作");
                }

                //查询所有服务
                List<JndcClientServiceDO> jndcClientServiceDOS = jndcClientServiceDao.listByClientId(dbData.getId());
                jndcClientDTO.setClientServices(jndcClientServiceDOS);
                jndcClientHolder.startClient(jndcClientDTO);
            } else if (JNDCClientStatusEnum.PAUSE.value.equals(clientStatus)) {
                // todo 停止服务
                jndcClientHolder.stopClient(jndcClientDTO);
            }
        }
    }

    /**
     * 删除
     */
    @Override
    public void removeById(Serializable id) {
        // 确认存在
        JndcClientDTO byId = getById(id);

        if (JNDCClientStatusEnum.CONNECT.value.equals(byId.getClientStatus())) {
            throw new BizException("请先断开连接");
        }

        List<JndcClientServiceDO> jndcClientServiceDOS = jndcClientServiceDao.listByClientId(id);
        if (!jndcClientServiceDOS.isEmpty()) {
            throw new BizException("该客户端存在" + jndcClientServiceDOS.size() + "个注册服务，请先删除注册的服务");
        }


        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcClientDao.deleteById(id);
    }

    /**
     * 获取单条数据
     */
    @Override
    public JndcClientDTO getById(Serializable id) {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
        JndcClientDO JndcClientDO = jndcClientDao.selectById(id);
        if (JndcClientDO == null) {
            throw new BizException("数据不存在");
        }
        return JndcClientStructMapper.INSTANCE.toDTO(JndcClientDO);
    }

    @Override
    public void resetAllClientStatus() {
        DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
        jndcClientDao.resetAllClientStatus();
    }

    @Override
    public void forceStopOperation(Long id) {
        JndcClientDTO byId = getById(id);
        if (JNDCClientStatusEnum.PAUSE.value.equals(byId.getClientStatus())) {
            throw new BizException("只能停止连接或处理中的客户端");
        }
        byId.setClientStatus(JNDCClientStatusEnum.PAUSE.value);
        //复用逻辑，存在重复查询，由于次数较少，暂不优化
        updateById(byId);
    }

    @Override
    public void connectOperation(Long id) {
        JndcClientDTO byId = getById(id);
        if (!JNDCClientStatusEnum.PAUSE.value.equals(byId.getClientStatus())) {
            throw new BizException("只能启动停止的客户端");
        }
        byId.setClientStatus(JNDCClientStatusEnum.CONNECT.value);
        //复用逻辑，存在重复查询，由于次数较少，暂不优化
        updateById(byId);
    }
}
