package com.view.jndc.manage.serviceI.jndc_clietn_service.Impl;

import com.view.jndc.manage.model.jndc_clietn_service.JndcClietnServiceStructMapper;
import com.view.jndc.manage.dao.jndc_clietn_service.JndcClietnServiceDao;
import com.view.jndc.manage.model.jndc_clietn_service.vo.JndcClietnServiceVO;
import com.view.jndc.manage.model.jndc_clietn_service.d_o.JndcClietnServiceDO;
import com.view.jndc.manage.model.jndc_clietn_service.dto.JndcClietnServiceDTO;
import com.view.free_lite.common.config.dynamic_datasource.DynamicDataSource;
import com.view.free_lite.common.config.exception.BizException;
import com.view.jndc.manage.serviceI.jndc_clietn_service.JndcClietnServiceServiceI;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;
import java.util.stream.Collectors;
import java.io.Serializable;

@Service
@RequiredArgsConstructor
public class JndcClietnServiceServiceImpl implements JndcClietnServiceServiceI {

  private final JndcClietnServiceDao jndcClietnServiceDao;

  /** 分页查询 */
  @Override
  public IPage queryPage(JndcClietnServiceDTO jndcClietnServiceDTO) {
    JndcClietnServiceDO copy = JndcClietnServiceStructMapper.INSTANCE.toDO(jndcClietnServiceDTO);
    Page page = new Page(jndcClietnServiceDTO.getCurrent(), jndcClietnServiceDTO.getSize());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    IPage<JndcClietnServiceDO> pageResult = jndcClietnServiceDao.listPage(page, copy);
    IPage<JndcClietnServiceVO> convert =
        pageResult.convert(x -> JndcClietnServiceStructMapper.INSTANCE.toVO(x));
    return convert;
  }

  /** 查询 */
  @Override
  public List<JndcClietnServiceVO> queryList(JndcClietnServiceDTO jndcClietnServiceDTO) {
    JndcClietnServiceDO copy = JndcClietnServiceStructMapper.INSTANCE.toDO(jndcClietnServiceDTO);
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    List<JndcClietnServiceDO> list = jndcClietnServiceDao.list(copy);
    List<JndcClietnServiceVO> collect =
        list.stream()
            .map(x -> JndcClietnServiceStructMapper.INSTANCE.toVO(x))
            .collect(Collectors.toList());
    return collect;
  }

  /** 保存 */
  @Override
  public JndcClietnServiceDO save(JndcClietnServiceDTO jndcClietnServiceDTO) {
    JndcClietnServiceDO copy = JndcClietnServiceStructMapper.INSTANCE.toDO(jndcClietnServiceDTO);
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcClietnServiceDao.insert(copy);
    return copy;
  }

  /** 修改 */
  @Override
  public void updateById(JndcClietnServiceDTO jndcClietnServiceDTO) {
    // 确认存在
    getById(jndcClietnServiceDTO.getId());

    JndcClietnServiceDO copy = JndcClietnServiceStructMapper.INSTANCE.toDO(jndcClietnServiceDTO);
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcClietnServiceDao.updateById(copy);
  }

  /** 删除 */
  @Override
  public void removeById(Serializable id) {
    // 确认存在
    getById(id);

    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcClietnServiceDao.deleteById(id);
  }

  /** 获取单条数据 */
  @Override
  public JndcClietnServiceDTO getById(Serializable id) {
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    JndcClietnServiceDO JndcClietnServiceDO = jndcClietnServiceDao.selectById(id);
    if (JndcClietnServiceDO == null) {
      throw new BizException("数据不存在");
    }
    return JndcClietnServiceStructMapper.INSTANCE.toDTO(JndcClietnServiceDO);
  }
}
