package com.view.jndc.manage.model.jndc_server_app;

import com.view.jndc.manage.model.jndc_server_app.vo.JndcServerAppVO;
import com.view.jndc.manage.model.jndc_server_app.d_o.JndcServerAppDO;
import com.view.jndc.manage.model.jndc_server_app.dto.JndcServerAppDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcServerAppStructMapper {

  public static final JndcServerAppStructMapper INSTANCE =
      Mappers.getMapper(JndcServerAppStructMapper.class);

  /**
   * dto转do
   *
   * @param exampleDTO
   * @return
   */
  JndcServerAppDO toDO(JndcServerAppDTO exampleDTO);

  /**
   * do 转dto
   *
   * @param exampleDO
   * @return
   */
  JndcServerAppDTO toDTO(JndcServerAppDO exampleDO);

  /**
   * dto转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcServerAppVO toVO(JndcServerAppDTO exampleDTO);

  /**
   * do转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcServerAppVO toVO(JndcServerAppDO exampleDTO);
}
