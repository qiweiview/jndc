package com.view.jndc.manage.model.jndc_clietn_service.d_o;

import com.view.jndc.manage.model.jndc_clietn_service.JndcClietnServiceStructMapper;
import com.view.jndc.manage.model.jndc_clietn_service.dto.JndcClietnServiceDTO;
import com.view.jndc.manage.model.jndc_clietn_service.vo.JndcClietnServiceVO;
import com.baomidou.mybatisplus.annotation.*;
import java.sql.Timestamp;
import lombok.Data;

@TableName("jndc_clietn_service")
@Data
public class JndcClietnServiceDO {
  /** 是否自动注册 */
  @TableField(value = "auto_register")
  private Integer autoRegister;

  /** 所属客户端id */
  @TableField(value = "belong_client_id")
  private Long belongClientId;

  /** 客户端唯一id */
  @TableField(value = "client_unique_id")
  private String clientUniqueId;

  /** 创建时间 */
  @TableField(value = "create_time")
  private java.time.LocalDateTime createTime;

  /** */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /** 服务主机 */
  @TableField(value = "servcie_host")
  private String servcieHost;

  /** 服务名称 */
  @TableField(value = "service_name")
  private String serviceName;

  /** 服务端口 */
  @TableField(value = "service_port")
  private String servicePort;

  /** 服务状态 */
  @TableField(value = "service_status")
  private String serviceStatus;

  /** 修改时间 */
  @TableField(value = "update_time")
  private java.time.LocalDateTime updateTime;

  public JndcClietnServiceDTO toDTO() {
    return JndcClietnServiceStructMapper.INSTANCE.toDTO(this);
  }

  public JndcClietnServiceVO toVO() {
    return JndcClietnServiceStructMapper.INSTANCE.toVO(this);
  }
}
