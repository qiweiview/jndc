package com.view.jndc.manage.serviceI.jndc_client.Impl;

import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.free_lite.common.config.exception.BizException;
import com.view.jndc.manage.model.jndc_client.JndcClientStructMapper;
import com.view.jndc.manage.dao.jndc_client.JndcClientDao;
import com.view.jndc.manage.model.jndc_client.vo.JndcClientVO;
import com.view.jndc.manage.model.jndc_client.d_o.JndcClientDO;
import com.view.jndc.manage.model.jndc_client.dto.JndcClientDTO;

import com.view.jndc.manage.serviceI.jndc_client.JndcClientServiceI;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.stream.Collectors;
import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class JndcClientServiceImpl implements JndcClientServiceI {

  private final JndcClientDao jndcClientDao;

  /** 分页查询 */
  @Override
  public IPage queryPage(JndcClientDTO jndcClientDTO) {
    JndcClientDO copy = JndcClientStructMapper.INSTANCE.toDO(jndcClientDTO);
    Page page = new Page(jndcClientDTO.getCurrent(), jndcClientDTO.getSize());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    IPage<JndcClientDO> pageResult = jndcClientDao.listPage(page, copy);
    IPage<JndcClientVO> convert = pageResult.convert(x -> JndcClientStructMapper.INSTANCE.toVO(x));
    return convert;
  }

  /** 查询 */
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

  /** 保存 */
  @Override
  public JndcClientDO save(JndcClientDTO jndcClientDTO) {
    JndcClientDO copy = JndcClientStructMapper.INSTANCE.toDO(jndcClientDTO);
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcClientDao.insert(copy);
    return copy;
  }

  /** 修改 */
  @Override
  public void updateById(JndcClientDTO jndcClientDTO) {
    // 确认存在
    getById(jndcClientDTO.getId());

    JndcClientDO copy = JndcClientStructMapper.INSTANCE.toDO(jndcClientDTO);
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcClientDao.updateById(copy);
  }

  /** 删除 */
  @Override
  public void removeById(Serializable id) {
    // 确认存在
    getById(id);

    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcClientDao.deleteById(id);
  }

  /** 获取单条数据 */
  @Override
  public JndcClientDTO getById(Serializable id) {
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    JndcClientDO JndcClientDO = jndcClientDao.selectById(id);
    if (JndcClientDO == null) {
      throw new BizException("数据不存在");
    }
    return JndcClientStructMapper.INSTANCE.toDTO(JndcClientDO);
  }
}
