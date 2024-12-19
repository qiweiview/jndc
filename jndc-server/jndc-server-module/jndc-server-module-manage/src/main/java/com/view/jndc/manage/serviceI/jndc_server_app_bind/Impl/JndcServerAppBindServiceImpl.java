package com.view.jndc.manage.serviceI.jndc_server_app_bind.Impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.free_lite.common.config.exception.BizException;
import com.view.free_lite.common.utils.SnowflakeIdWorker;
import com.view.jndc.manage.dao.jndc_server_app_bind.JndcServerAppBindDao;
import com.view.jndc.manage.model.jndc_server_app_bind.JndcServerAppBindStructMapper;
import com.view.jndc.manage.model.jndc_server_app_bind.d_o.JndcServerAppBindDO;
import com.view.jndc.manage.model.jndc_server_app_bind.dto.JndcServerAppBindDTO;
import com.view.jndc.manage.model.jndc_server_app_bind.vo.JndcServerAppBindVO;
import com.view.jndc.manage.serviceI.jndc_server_app_bind.JndcServerAppBindServiceI;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JndcServerAppBindServiceImpl implements JndcServerAppBindServiceI {

  private final JndcServerAppBindDao jndcServerAppBindDao;

    private final SnowflakeIdWorker snowflakeIdWorker;

  /** 分页查询 */
  @Override
  public IPage queryPage(JndcServerAppBindDTO jndcServerAppBindDTO) {
    JndcServerAppBindDO copy = JndcServerAppBindStructMapper.INSTANCE.toDO(jndcServerAppBindDTO);
    Page page = new Page(jndcServerAppBindDTO.getCurrent(), jndcServerAppBindDTO.getSize());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    IPage<JndcServerAppBindDO> pageResult = jndcServerAppBindDao.listPage(page, copy);
    IPage<JndcServerAppBindVO> convert =
        pageResult.convert(x -> JndcServerAppBindStructMapper.INSTANCE.toVO(x));
    return convert;
  }

  /** 查询 */
  @Override
  public List<JndcServerAppBindVO> queryList(JndcServerAppBindDTO jndcServerAppBindDTO) {
    JndcServerAppBindDO copy = JndcServerAppBindStructMapper.INSTANCE.toDO(jndcServerAppBindDTO);
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    List<JndcServerAppBindDO> list = jndcServerAppBindDao.list(copy);
    List<JndcServerAppBindVO> collect =
        list.stream()
            .map(x -> JndcServerAppBindStructMapper.INSTANCE.toVO(x))
            .collect(Collectors.toList());
    return collect;
  }

  /** 保存 */
  @Override
  public JndcServerAppBindDO save(JndcServerAppBindDTO jndcServerAppBindDTO) {
    JndcServerAppBindDO copy = JndcServerAppBindStructMapper.INSTANCE.toDO(jndcServerAppBindDTO);
      copy.setId(snowflakeIdWorker.nextId());
      copy.setCreateTime(LocalDateTime.now());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcServerAppBindDao.insert(copy);
    return copy;
  }

  /** 修改 */
  @Override
  public void updateById(JndcServerAppBindDTO jndcServerAppBindDTO) {
    // 确认存在
    getById(jndcServerAppBindDTO.getId());

    JndcServerAppBindDO copy = JndcServerAppBindStructMapper.INSTANCE.toDO(jndcServerAppBindDTO);
      copy.setUpdateTime(LocalDateTime.now());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcServerAppBindDao.updateById(copy);
  }

  /** 删除 */
  @Override
  public void removeById(Serializable id) {
    // 确认存在
    getById(id);

    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcServerAppBindDao.deleteById(id);
  }

  /** 获取单条数据 */
  @Override
  public JndcServerAppBindDTO getById(Serializable id) {
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    JndcServerAppBindDO JndcServerAppBindDO = jndcServerAppBindDao.selectById(id);
    if (JndcServerAppBindDO == null) {
      throw new BizException("数据不存在");
    }
    return JndcServerAppBindStructMapper.INSTANCE.toDTO(JndcServerAppBindDO);
  }
}
