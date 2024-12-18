package com.view.jndc.manage.serviceI.jndc_server.Impl;

import com.view.free_lite.common.utils.SnowflakeIdWorker;
import com.view.jndc.manage.model.jndc_server.JndcServerStructMapper;
import com.view.jndc.manage.dao.jndc_server.JndcServerDao;
import com.view.jndc.manage.model.jndc_server.vo.JndcServerVO;
import com.view.jndc.manage.model.jndc_server.d_o.JndcServerDO;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.free_lite.common.config.exception.BizException;
import com.view.jndc.manage.serviceI.jndc_server.JndcServerServiceI;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class JndcServerServiceImpl implements JndcServerServiceI {

  private final JndcServerDao jndcServerDao;

  private final SnowflakeIdWorker snowflakeIdWorker;

  /** 分页查询 */
  @Override
  public IPage queryPage(JndcServerDTO jndcServerDTO) {
    JndcServerDO copy = JndcServerStructMapper.INSTANCE.toDO(jndcServerDTO);
    Page page = new Page(jndcServerDTO.getCurrent(), jndcServerDTO.getSize());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    IPage<JndcServerDO> pageResult = jndcServerDao.listPage(page, copy);
    IPage<JndcServerVO> convert = pageResult.convert(x -> JndcServerStructMapper.INSTANCE.toVO(x));
    return convert;
  }

  /** 查询 */
  @Override
  public List<JndcServerVO> queryList(JndcServerDTO jndcServerDTO) {
    JndcServerDO copy = JndcServerStructMapper.INSTANCE.toDO(jndcServerDTO);
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    List<JndcServerDO> list = jndcServerDao.list(copy);
    List<JndcServerVO> collect =
        list.stream()
            .map(x -> JndcServerStructMapper.INSTANCE.toVO(x))
            .collect(Collectors.toList());
    return collect;
  }

  /** 保存 */
  @Override
  public JndcServerDO save(JndcServerDTO jndcServerDTO) {
    JndcServerDO copy = JndcServerStructMapper.INSTANCE.toDO(jndcServerDTO);
    copy.setId(snowflakeIdWorker.nextId());
    copy.setCreateTime(LocalDateTime.now());

    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcServerDao.insert(copy);
    return copy;
  }

  /** 修改 */
  @Override
  public void updateById(JndcServerDTO jndcServerDTO) {
    // 确认存在
    getById(jndcServerDTO.getId());

    JndcServerDO copy = JndcServerStructMapper.INSTANCE.toDO(jndcServerDTO);
    copy.setUpdateTime(LocalDateTime.now());

    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcServerDao.updateById(copy);
  }

  /** 删除 */
  @Override
  public void removeById(Serializable id) {
    // 确认存在
    getById(id);

    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcServerDao.deleteById(id);
  }

  /** 获取单条数据 */
  @Override
  public JndcServerDTO getById(Serializable id) {
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    JndcServerDO JndcServerDO = jndcServerDao.selectById(id);
    if (JndcServerDO == null) {
      throw new BizException("数据不存在");
    }
    return JndcServerStructMapper.INSTANCE.toDTO(JndcServerDO);
  }
}
