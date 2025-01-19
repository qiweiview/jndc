package com.view.jndc.manage.model.jndc_server_service;

import com.view.jndc.manage.model.jndc_server_service.vo.JndcServerServiceVO;
import com.view.jndc.manage.model.jndc_server_service.d_o.JndcServerServiceDO;
import com.view.jndc.manage.model.jndc_server_service.dto.JndcServerServiceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcServerServiceStructMapper {

  public static final JndcServerServiceStructMapper INSTANCE =
      Mappers.getMapper(JndcServerServiceStructMapper.class);

  /**
   * dto转do
   *
   * @param exampleDTO
   * @return
   */
  JndcServerServiceDO toDO(JndcServerServiceDTO exampleDTO);

  /**
   * do 转dto
   *
   * @param exampleDO
   * @return
   */
  JndcServerServiceDTO toDTO(JndcServerServiceDO exampleDO);

  /**
   * dto转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcServerServiceVO toVO(JndcServerServiceDTO exampleDTO);

  /**
   * do转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcServerServiceVO toVO(JndcServerServiceDO exampleDTO);
}
