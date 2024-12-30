package com.view.jndc.manage.model.jndc_server_app.d_o;

import com.view.jndc.manage.model.jndc_server_app.JndcServerAppStructMapper;
import com.view.jndc.manage.model.jndc_server_app.dto.JndcServerAppDTO;
import com.view.jndc.manage.model.jndc_server_app.vo.JndcServerAppVO;
import com.baomidou.mybatisplus.annotation.*;
import java.sql.Timestamp;
import lombok.Data;

@TableName("jndc_server_app")
@Data
public class JndcServerAppDO {
  /** 监听域名 */
  @TableField(value = "bind_host")
  private String bindHost;

  /** 监听端口 */
  @TableField(value = "bind_port")
  private Integer bindPort;

  /** 监听状态 */
  @TableField(value = "bind_status")
  private String bindStatus;

  /** 创建时间 */
  @TableField(value = "create_time")
  private java.time.LocalDateTime createTime;

  /** id */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /** jndc服务id */
  @TableField(value = "server_id")
  private Long serverId;

  /** 来源客户端 */
  @TableField(value = "source_client_id")
  private String sourceClientId;

  /** 来源服务 */
  @TableField(value = "source_service_id")
  private String sourceServiceId;

  public JndcServerAppDTO toDTO() {
    return JndcServerAppStructMapper.INSTANCE.toDTO(this);
  }

  public JndcServerAppVO toVO() {
    return JndcServerAppStructMapper.INSTANCE.toVO(this);
  }
}
