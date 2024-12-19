package com.view.jndc.manage.model.jndc_client_service.d_o;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.manage.model.jndc_client_service.JndcClientServiceStructMapper;
import com.view.jndc.manage.model.jndc_client_service.dto.JndcClientServiceDTO;
import com.view.jndc.manage.model.jndc_client_service.vo.JndcClientServiceVO;
import lombok.Data;

@TableName("jndc_client_service")
@Data
public class JndcClientServiceDO {
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
  @TableField(value = "service_host")
  private String serviceHost;

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

    public JndcClientServiceDTO toDTO() {
        return JndcClientServiceStructMapper.INSTANCE.toDTO(this);
  }

    public JndcClientServiceVO toVO() {
        return JndcClientServiceStructMapper.INSTANCE.toVO(this);
  }
}
