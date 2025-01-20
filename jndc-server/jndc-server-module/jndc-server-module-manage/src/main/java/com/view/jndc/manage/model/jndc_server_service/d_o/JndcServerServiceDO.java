package com.view.jndc.manage.model.jndc_server_service.d_o;

import com.view.jndc.manage.model.jndc_server_service.JndcServerServiceStructMapper;
import com.view.jndc.manage.model.jndc_server_service.dto.JndcServerServiceDTO;
import com.view.jndc.manage.model.jndc_server_service.vo.JndcServerServiceVO;
import com.baomidou.mybatisplus.annotation.*;
import java.sql.Timestamp;
import lombok.Data;

@TableName("jndc_server_service")
@Data
public class JndcServerServiceDO {
  /** id */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /** 创建时间 */
  @TableField(value = "create_time")
  private java.time.LocalDateTime createTime;

  /** 修改时间 */
  @TableField(value = "update_time")
  private java.time.LocalDateTime updateTime;

  /** 客户端唯一id */
  @TableField(value = "client_unique_id")
  private String clientUniqueId;

  /** 服务名称 */
  @TableField(value = "service_name")
  private String serviceName;

  /** 服务主机 */
  @TableField(value = "service_host")
  private String serviceHost;

  /** 服务端口 */
  @TableField(value = "service_port")
  private Integer servicePort;

  /** 期望端口 */
  @TableField(value = "expect_port")
  private Integer expectPort;

  /** 服务状态 */
  @TableField(value = "service_status")
  private String serviceStatus;

  /** 服务协议 */
  @TableField(value = "service_protocol")
  private String serviceProtocol;

  /** 服务模式 */
  @TableField(value = "service_mode")
  private String serviceMode;

  /** 服务唯一id */
  @TableField(value = "service_unique_id")
  private String serviceUniqueId;

  /** 服务器唯一id */
  @TableField(value = "server_unique_id")
  private String serverUniqueId;

  public JndcServerServiceDTO toDTO() {
    return JndcServerServiceStructMapper.INSTANCE.toDTO(this);
  }

  public JndcServerServiceVO toVO() {
    return JndcServerServiceStructMapper.INSTANCE.toVO(this);
  }
}
