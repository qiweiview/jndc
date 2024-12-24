package com.view.jndc.manage.model.jndc_rule_ip;

import com.view.jndc.manage.model.jndc_rule_ip.vo.JndcRuleIpVO;
import com.view.jndc.manage.model.jndc_rule_ip.d_o.JndcRuleIpDO;
import com.view.jndc.manage.model.jndc_rule_ip.dto.JndcRuleIpDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcRuleIpStructMapper {

  public static final JndcRuleIpStructMapper INSTANCE =
      Mappers.getMapper(JndcRuleIpStructMapper.class);

  /**
   * dto转do
   *
   * @param exampleDTO
   * @return
   */
  JndcRuleIpDO toDO(JndcRuleIpDTO exampleDTO);

  /**
   * do 转dto
   *
   * @param exampleDO
   * @return
   */
  JndcRuleIpDTO toDTO(JndcRuleIpDO exampleDO);

  /**
   * dto转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcRuleIpVO toVO(JndcRuleIpDTO exampleDTO);

  /**
   * do转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcRuleIpVO toVO(JndcRuleIpDO exampleDTO);
}
