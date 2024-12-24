package com.view.jndc.manage.serviceI.jndc_rule_ip.Impl;

import com.view.jndc.manage.model.jndc_rule_ip.JndcRuleIpStructMapper;
import com.view.jndc.manage.dao.jndc_rule_ip.JndcRuleIpDao;
import com.view.jndc.manage.model.jndc_rule_ip.vo.JndcRuleIpVO;
import com.view.jndc.manage.model.jndc_rule_ip.d_o.JndcRuleIpDO;
import com.view.jndc.manage.model.jndc_rule_ip.dto.JndcRuleIpDTO;
// import com.view.jndc.manage.config.dynamic_datasource.DynamicDataSource;
import com.view.jndc.manage.serviceI.jndc_rule_ip.JndcRuleIpServiceI;
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
public class JndcRuleIpServiceImpl implements JndcRuleIpServiceI {

  private final JndcRuleIpDao jndcRuleIpDao;

  private final SnowflakeIdWorker snowflakeIdWorker;

  /** 分页查询 */
  @Override
  public IPage queryPage(JndcRuleIpDTO jndcRuleIpDTO) {
    JndcRuleIpDO copy = JndcRuleIpStructMapper.INSTANCE.toDO(jndcRuleIpDTO);
    Page page = new Page(jndcRuleIpDTO.getCurrent(), jndcRuleIpDTO.getSize());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    IPage<JndcRuleIpDO> pageResult = jndcRuleIpDao.listPage(page, copy);
    IPage<JndcRuleIpVO> convert = pageResult.convert(x -> JndcRuleIpStructMapper.INSTANCE.toVO(x));
    return convert;
  }

  /** 查询 */
  @Override
  public List<JndcRuleIpVO> queryList(JndcRuleIpDTO jndcRuleIpDTO) {
    JndcRuleIpDO copy = JndcRuleIpStructMapper.INSTANCE.toDO(jndcRuleIpDTO);
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    List<JndcRuleIpDO> list = jndcRuleIpDao.list(copy);
    List<JndcRuleIpVO> collect =
        list.stream()
            .map(x -> JndcRuleIpStructMapper.INSTANCE.toVO(x))
            .collect(Collectors.toList());
    return collect;
  }

  /** 保存 */
  @Override
  public JndcRuleIpDO save(JndcRuleIpDTO jndcRuleIpDTO) {
    JndcRuleIpDO copy = JndcRuleIpStructMapper.INSTANCE.toDO(jndcRuleIpDTO);
    copy.setId(snowflakeIdWorker.nextId());
    copy.setCreateTime(LocalDateTime.now());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcRuleIpDao.insert(copy);
    return copy;
  }

  /** 修改 */
  @Override
  public void updateById(JndcRuleIpDTO jndcRuleIpDTO) {
    // 确认存在
    getById(jndcRuleIpDTO.getId());

    JndcRuleIpDO copy = JndcRuleIpStructMapper.INSTANCE.toDO(jndcRuleIpDTO);
    copy.setUpdateTime(LocalDateTime.now());
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcRuleIpDao.updateById(copy);
  }

  /** 删除 */
  @Override
  public void removeById(Serializable id) {
    // 确认存在
    getById(id);

    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_WRITE);
    jndcRuleIpDao.deleteById(id);
  }

  /** 获取单条数据 */
  @Override
  public JndcRuleIpDTO getById(Serializable id) {
    DynamicDataSource.setDataSourceKey(DynamicDataSource.DB_READ);
    JndcRuleIpDO JndcRuleIpDO = jndcRuleIpDao.selectById(id);
    if (JndcRuleIpDO == null) {
      throw new BizException("数据不存在");
    }
    return JndcRuleIpStructMapper.INSTANCE.toDTO(JndcRuleIpDO);
  }
}
