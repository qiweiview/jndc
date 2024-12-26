package com.view.jndc.manage.model.jndc_access_history;

import com.view.jndc.manage.model.jndc_access_history.vo.JndcAccessHistoryVO;
import com.view.jndc.manage.model.jndc_access_history.d_o.JndcAccessHistoryDO;
import com.view.jndc.manage.model.jndc_access_history.dto.JndcAccessHistoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcAccessHistoryStructMapper {

  public static final JndcAccessHistoryStructMapper INSTANCE =
      Mappers.getMapper(JndcAccessHistoryStructMapper.class);

  /**
   * dto转do
   *
   * @param exampleDTO
   * @return
   */
  JndcAccessHistoryDO toDO(JndcAccessHistoryDTO exampleDTO);

  /**
   * do 转dto
   *
   * @param exampleDO
   * @return
   */
  JndcAccessHistoryDTO toDTO(JndcAccessHistoryDO exampleDO);

  /**
   * dto转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcAccessHistoryVO toVO(JndcAccessHistoryDTO exampleDTO);

  /**
   * do转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcAccessHistoryVO toVO(JndcAccessHistoryDO exampleDTO);
}
