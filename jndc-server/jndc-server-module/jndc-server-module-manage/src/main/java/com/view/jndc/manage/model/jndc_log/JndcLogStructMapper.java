package com.view.jndc.manage.model.jndc_log;

import com.view.jndc.manage.model.jndc_log.d_o.JndcLogDO;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.model.jndc_log.vo.JndcLogVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcLogStructMapper {

    public static final JndcLogStructMapper INSTANCE = Mappers.getMapper(JndcLogStructMapper.class);

    /**
     * dto转do
     *
     * @param exampleDTO
     * @return
     */
    JndcLogDO toDO(JndcLogDTO exampleDTO);

    /**
     * do 转dto
     *
     * @param exampleDO
     * @return
     */
    JndcLogDTO toDTO(JndcLogDO exampleDO);

    /**
     * dto转vo
     *
     * @param exampleDTO
     * @return
     */
    JndcLogVO toVO(JndcLogDTO exampleDTO);

    /**
     * do转vo
     *
     * @param exampleDTO
     * @return
     */
    JndcLogVO toVO(JndcLogDO exampleDTO);
}
