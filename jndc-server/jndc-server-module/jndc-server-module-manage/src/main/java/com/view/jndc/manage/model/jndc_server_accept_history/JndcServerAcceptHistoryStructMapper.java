package com.view.jndc.manage.model.jndc_server_accept_history;

import com.view.jndc.manage.model.jndc_server_accept_history.vo.JndcServerAcceptHistoryVO;
import com.view.jndc.manage.model.jndc_server_accept_history.d_o.JndcServerAcceptHistoryDO;
import com.view.jndc.manage.model.jndc_server_accept_history.dto.JndcServerAcceptHistoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JndcServerAcceptHistoryStructMapper {

  public static final JndcServerAcceptHistoryStructMapper INSTANCE =
      Mappers.getMapper(JndcServerAcceptHistoryStructMapper.class);

  /**
   * dto转do
   *
   * @param exampleDTO
   * @return
   */
  JndcServerAcceptHistoryDO toDO(JndcServerAcceptHistoryDTO exampleDTO);

  /**
   * do 转dto
   *
   * @param exampleDO
   * @return
   */
  JndcServerAcceptHistoryDTO toDTO(JndcServerAcceptHistoryDO exampleDO);

  /**
   * dto转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcServerAcceptHistoryVO toVO(JndcServerAcceptHistoryDTO exampleDTO);

  /**
   * do转vo
   *
   * @param exampleDTO
   * @return
   */
  JndcServerAcceptHistoryVO toVO(JndcServerAcceptHistoryDO exampleDTO);
}
