package com.view.jndc.manage.model.jndc_server;

import com.view.jndc.manage.model.jndc_server.vo.JndcServerVO;
import com.view.jndc.manage.model.jndc_server.d_o.JndcServerDO;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcServerStructMapper {

  public static final JndcServerStructMapper INSTANCE =
      Mappers.getMapper(JndcServerStructMapper.class);

  /**
   * dto转do
   *
   * @param exampleDTO
   * @return
   */
  JndcServerDO toDO(JndcServerDTO exampleDTO);

  /**
   * do 转dto
   *
   * @param exampleDO
   * @return
   */
  JndcServerDTO toDTO(JndcServerDO exampleDO);

  /**
   * dto转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcServerVO toVO(JndcServerDTO exampleDTO);

  /**
   * do转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcServerVO toVO(JndcServerDO exampleDTO);
}
