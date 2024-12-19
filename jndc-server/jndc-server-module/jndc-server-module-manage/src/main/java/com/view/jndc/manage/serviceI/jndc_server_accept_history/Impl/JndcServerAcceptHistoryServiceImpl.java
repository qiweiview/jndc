package com.view.jndc.manage.serviceI.jndc_server_accept_history.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.free_lite.common.config.exception.BizException;
import com.view.free_lite.common.utils.SnowflakeIdWorker;
import com.view.jndc.manage.dao.jndc_server_accept_history.JndcServerAcceptHistoryDao;
import com.view.jndc.manage.model.jndc_server_accept_history.JndcServerAcceptHistoryStructMapper;
import com.view.jndc.manage.model.jndc_server_accept_history.d_o.JndcServerAcceptHistoryDO;
import com.view.jndc.manage.model.jndc_server_accept_history.dto.JndcServerAcceptHistoryDTO;
import com.view.jndc.manage.model.jndc_server_accept_history.vo.JndcServerAcceptHistoryVO;
import com.view.jndc.manage.serviceI.jndc_server_accept_history.JndcServerAcceptHistoryServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JndcServerAcceptHistoryServiceImpl implements JndcServerAcceptHistoryServiceI {

  private final JndcServerAcceptHistoryDao jndcServerAcceptHistoryDao;

    private final SnowflakeIdWorker snowflakeIdWorker;

  /** 分页查询 */
  @Override
  public IPage queryPage(JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO) {
    JndcServerAcceptHistoryDO copy =
        JndcServerAcceptHistoryStructMapper.INSTANCE.toDO(jndcServerAcceptHistoryDTO);
    Page page =
        new Page(jndcServerAcceptHistoryDTO.getCurrent(), jndcServerAcceptHistoryDTO.getSize());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    IPage<JndcServerAcceptHistoryDO> pageResult = jndcServerAcceptHistoryDao.listPage(page, copy);
    IPage<JndcServerAcceptHistoryVO> convert =
        pageResult.convert(x -> JndcServerAcceptHistoryStructMapper.INSTANCE.toVO(x));
    return convert;
  }

  /** 查询 */
  @Override
  public List<JndcServerAcceptHistoryVO> queryList(
      JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO) {
    JndcServerAcceptHistoryDO copy =
        JndcServerAcceptHistoryStructMapper.INSTANCE.toDO(jndcServerAcceptHistoryDTO);
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    List<JndcServerAcceptHistoryDO> list = jndcServerAcceptHistoryDao.list(copy);
    List<JndcServerAcceptHistoryVO> collect =
        list.stream()
            .map(x -> JndcServerAcceptHistoryStructMapper.INSTANCE.toVO(x))
            .collect(Collectors.toList());
    return collect;
  }

  /** 保存 */
  @Override
  public JndcServerAcceptHistoryDO save(JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO) {
    JndcServerAcceptHistoryDO copy =
        JndcServerAcceptHistoryStructMapper.INSTANCE.toDO(jndcServerAcceptHistoryDTO);
      copy.setId(snowflakeIdWorker.nextId());
      copy.setCreateTime(LocalDateTime.now());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcServerAcceptHistoryDao.insert(copy);
    return copy;
  }

  /** 修改 */
  @Override
  public void updateById(JndcServerAcceptHistoryDTO jndcServerAcceptHistoryDTO) {
    // 确认存在
    getById(jndcServerAcceptHistoryDTO.getId());

    JndcServerAcceptHistoryDO copy =
        JndcServerAcceptHistoryStructMapper.INSTANCE.toDO(jndcServerAcceptHistoryDTO);
      copy.setUpdateTime(LocalDateTime.now());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcServerAcceptHistoryDao.updateById(copy);
  }

  /** 删除 */
  @Override
  public void removeById(Serializable id) {
    // 确认存在
    getById(id);

    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcServerAcceptHistoryDao.deleteById(id);
  }

  /** 获取单条数据 */
  @Override
  public JndcServerAcceptHistoryDTO getById(Serializable id) {
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    JndcServerAcceptHistoryDO JndcServerAcceptHistoryDO = jndcServerAcceptHistoryDao.selectById(id);
    if (JndcServerAcceptHistoryDO == null) {
      throw new BizException("数据不存在");
    }
    return JndcServerAcceptHistoryStructMapper.INSTANCE.toDTO(JndcServerAcceptHistoryDO);
  }
}
