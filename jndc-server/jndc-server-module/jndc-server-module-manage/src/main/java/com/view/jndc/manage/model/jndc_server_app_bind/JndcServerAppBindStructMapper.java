package com.view.jndc.manage.model.jndc_server_app_bind;

import com.view.jndc.manage.model.jndc_server_app_bind.vo.JndcServerAppBindVO;
import com.view.jndc.manage.model.jndc_server_app_bind.d_o.JndcServerAppBindDO;
import com.view.jndc.manage.model.jndc_server_app_bind.dto.JndcServerAppBindDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcServerAppBindStructMapper {

  public static final JndcServerAppBindStructMapper INSTANCE =
      Mappers.getMapper(JndcServerAppBindStructMapper.class);

  /**
   * dto转do
   *
   * @param exampleDTO
   * @return
   */
  JndcServerAppBindDO toDO(JndcServerAppBindDTO exampleDTO);

  /**
   * do 转dto
   *
   * @param exampleDO
   * @return
   */
  JndcServerAppBindDTO toDTO(JndcServerAppBindDO exampleDO);

  /**
   * dto转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcServerAppBindVO toVO(JndcServerAppBindDTO exampleDTO);

  /**
   * do转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcServerAppBindVO toVO(JndcServerAppBindDO exampleDTO);
}
