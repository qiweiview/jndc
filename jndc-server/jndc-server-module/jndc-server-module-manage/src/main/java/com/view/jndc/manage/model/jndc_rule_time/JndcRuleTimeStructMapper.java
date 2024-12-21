package com.view.jndc.manage.model.jndc_rule_time;

import com.view.jndc.manage.model.jndc_rule_time.d_o.JndcRuleTimeDO;
import com.view.jndc.manage.model.jndc_rule_time.dto.JndcRuleTimeDTO;
import com.view.jndc.manage.model.jndc_rule_time.vo.JndcRuleTimeVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcRuleTimeStructMapper {

    public static final JndcRuleTimeStructMapper INSTANCE =
            Mappers.getMapper(JndcRuleTimeStructMapper.class);

    /**
     * dto转do
     *
     * @param exampleDTO
     * @return
     */
    JndcRuleTimeDO toDO(JndcRuleTimeDTO exampleDTO);

    /**
     * do 转dto
     *
     * @param exampleDO
     * @return
     */
    JndcRuleTimeDTO toDTO(JndcRuleTimeDO exampleDO);

    /**
     * dto转vo
     *
     * @param exampleDTO
     * @return
     */
    JndcRuleTimeVO toVO(JndcRuleTimeDTO exampleDTO);

    /**
     * do转vo
     *
     * @param exampleDTO
     * @return
     */
    JndcRuleTimeVO toVO(JndcRuleTimeDO exampleDTO);
}
