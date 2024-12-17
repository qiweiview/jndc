package com.view.jndc.manage.model.jndc_client;

import com.view.jndc.manage.model.jndc_client.vo.JndcClientVO;
import com.view.jndc.manage.model.jndc_client.d_o.JndcClientDO;
import com.view.jndc.manage.model.jndc_client.dto.JndcClientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcClientStructMapper {

  public static final JndcClientStructMapper INSTANCE =
      Mappers.getMapper(JndcClientStructMapper.class);

  /**
   * dto转do
   *
   * @param exampleDTO
   * @return
   */
  JndcClientDO toDO(JndcClientDTO exampleDTO);

  /**
   * do 转dto
   *
   * @param exampleDO
   * @return
   */
  JndcClientDTO toDTO(JndcClientDO exampleDO);

  /**
   * dto转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcClientVO toVO(JndcClientDTO exampleDTO);

  /**
   * do转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcClientVO toVO(JndcClientDO exampleDTO);
}
