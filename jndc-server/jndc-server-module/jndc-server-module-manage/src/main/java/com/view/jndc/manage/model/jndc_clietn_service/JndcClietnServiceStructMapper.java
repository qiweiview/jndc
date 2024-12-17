package com.view.jndc.manage.model.jndc_clietn_service;

import com.view.jndc.manage.model.jndc_clietn_service.vo.JndcClietnServiceVO;
import com.view.jndc.manage.model.jndc_clietn_service.d_o.JndcClietnServiceDO;
import com.view.jndc.manage.model.jndc_clietn_service.dto.JndcClietnServiceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcClietnServiceStructMapper {

  public static final JndcClietnServiceStructMapper INSTANCE =
      Mappers.getMapper(JndcClietnServiceStructMapper.class);

  /**
   * dto转do
   *
   * @param exampleDTO
   * @return
   */
  JndcClietnServiceDO toDO(JndcClietnServiceDTO exampleDTO);

  /**
   * do 转dto
   *
   * @param exampleDO
   * @return
   */
  JndcClietnServiceDTO toDTO(JndcClietnServiceDO exampleDO);

  /**
   * dto转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcClietnServiceVO toVO(JndcClietnServiceDTO exampleDTO);

  /**
   * do转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcClietnServiceVO toVO(JndcClietnServiceDO exampleDTO);
}
