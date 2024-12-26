package com.view.jndc.manage.serviceI.jndc_access_history.Impl;

import com.view.jndc.manage.model.jndc_access_history.JndcAccessHistoryStructMapper;
import com.view.jndc.manage.dao.jndc_access_history.JndcAccessHistoryDao;
import com.view.jndc.manage.model.jndc_access_history.vo.JndcAccessHistoryVO;
import com.view.jndc.manage.model.jndc_access_history.d_o.JndcAccessHistoryDO;
import com.view.jndc.manage.model.jndc_access_history.dto.JndcAccessHistoryDTO;
// import com.view.jndc.manage.config.dynamic_datasource.DynamicDataSource;
import com.view.jndc.manage.serviceI.jndc_access_history.JndcAccessHistoryServiceI;
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
public class JndcAccessHistoryServiceImpl implements JndcAccessHistoryServiceI {

  private final JndcAccessHistoryDao jndcAccessHistoryDao;

  private final SnowflakeIdWorker snowflakeIdWorker;

  /** 分页查询 */
  @Override
  public IPage queryPage(JndcAccessHistoryDTO jndcAccessHistoryDTO) {
    JndcAccessHistoryDO copy = JndcAccessHistoryStructMapper.INSTANCE.toDO(jndcAccessHistoryDTO);
    Page page = new Page(jndcAccessHistoryDTO.getCurrent(), jndcAccessHistoryDTO.getSize());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    IPage<JndcAccessHistoryDO> pageResult = jndcAccessHistoryDao.listPage(page, copy);
    IPage<JndcAccessHistoryVO> convert =
        pageResult.convert(x -> JndcAccessHistoryStructMapper.INSTANCE.toVO(x));
    return convert;
  }

  /** 查询 */
  @Override
  public List<JndcAccessHistoryVO> queryList(JndcAccessHistoryDTO jndcAccessHistoryDTO) {
    JndcAccessHistoryDO copy = JndcAccessHistoryStructMapper.INSTANCE.toDO(jndcAccessHistoryDTO);
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    List<JndcAccessHistoryDO> list = jndcAccessHistoryDao.list(copy);
    List<JndcAccessHistoryVO> collect =
        list.stream()
            .map(x -> JndcAccessHistoryStructMapper.INSTANCE.toVO(x))
            .collect(Collectors.toList());
    return collect;
  }

  /** 保存 */
  @Override
  public JndcAccessHistoryDO save(JndcAccessHistoryDTO jndcAccessHistoryDTO) {
    JndcAccessHistoryDO copy = JndcAccessHistoryStructMapper.INSTANCE.toDO(jndcAccessHistoryDTO);
    copy.setId(snowflakeIdWorker.nextId());
    copy.setCreateTime(LocalDateTime.now());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcAccessHistoryDao.insert(copy);
    return copy;
  }

  /** 修改 */
  @Override
  public void updateById(JndcAccessHistoryDTO jndcAccessHistoryDTO) {
    // 确认存在
    getById(jndcAccessHistoryDTO.getId());

    JndcAccessHistoryDO copy = JndcAccessHistoryStructMapper.INSTANCE.toDO(jndcAccessHistoryDTO);
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcAccessHistoryDao.updateById(copy);
  }

  /** 删除 */
  @Override
  public void removeById(Serializable id) {
    // 确认存在
    getById(id);

    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcAccessHistoryDao.deleteById(id);
  }

  /** 获取单条数据 */
  @Override
  public JndcAccessHistoryDTO getById(Serializable id) {
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    JndcAccessHistoryDO JndcAccessHistoryDO = jndcAccessHistoryDao.selectById(id);
    if (JndcAccessHistoryDO == null) {
      throw new BizException("数据不存在");
    }
    return JndcAccessHistoryStructMapper.INSTANCE.toDTO(JndcAccessHistoryDO);
  }
}
