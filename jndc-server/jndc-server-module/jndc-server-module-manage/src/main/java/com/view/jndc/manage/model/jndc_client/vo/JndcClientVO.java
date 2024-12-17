package com.view.jndc.manage.model.jndc_client.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class JndcClientVO {

  /** 服务主机 */
  private String bindServerHost;

  /** 服务端口 */
  private Integer bindServerPort;

  /** 客户端名称 */
  private String clientName;

  /** 客户端备注 */
  private String clientRemark;

  /** 客户端状态 */
  private String clientStatus;

  /** 客户端唯一编号 */
  private String clientUniqueId;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** 伪装协议 */
  private String disguisedProtocol;

  /** */
  private Long id;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;
}
