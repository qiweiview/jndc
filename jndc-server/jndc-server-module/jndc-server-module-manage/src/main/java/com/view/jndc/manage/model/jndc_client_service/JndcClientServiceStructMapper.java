package com.view.jndc.manage.model.jndc_client_service;

import com.view.jndc.manage.model.jndc_client_service.d_o.JndcClientServiceDO;
import com.view.jndc.manage.model.jndc_client_service.dto.JndcClientServiceDTO;
import com.view.jndc.manage.model.jndc_client_service.vo.JndcClientServiceVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcClientServiceStructMapper {

    public static final JndcClientServiceStructMapper INSTANCE =
            Mappers.getMapper(JndcClientServiceStructMapper.class);

    /**
     * dto转do
     *
     * @param exampleDTO
     * @return
     */
    JndcClientServiceDO toDO(JndcClientServiceDTO exampleDTO);

    /**
     * do 转dto
     *
     * @param exampleDO
     * @return
     */
    JndcClientServiceDTO toDTO(JndcClientServiceDO exampleDO);

    /**
     * dto转vo
     *
     * @param exampleDTO
     * @return
     */
    JndcClientServiceVO toVO(JndcClientServiceDTO exampleDTO);

    /**
     * do转vo
     *
     * @param exampleDTO
     * @return
     */
    JndcClientServiceVO toVO(JndcClientServiceDO exampleDTO);
}
