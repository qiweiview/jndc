package com.view.jndc.manage.model.jndc_client.d_o;

import com.view.jndc.manage.model.jndc_client.JndcClientStructMapper;
import com.view.jndc.manage.model.jndc_client.dto.JndcClientDTO;
import com.view.jndc.manage.model.jndc_client.vo.JndcClientVO;
import com.baomidou.mybatisplus.annotation.*;
import java.sql.Timestamp;
import lombok.Data;

@TableName("jndc_client")
@Data
public class JndcClientDO {
  /** 服务主机 */
  @TableField(value = "bind_server_host")
  private String bindServerHost;

  /** 服务端口 */
  @TableField(value = "bind_server_port")
  private Integer bindServerPort;

  /** 客户端名称 */
  @TableField(value = "client_name")
  private String clientName;

  /** 客户端备注 */
  @TableField(value = "client_remark")
  private String clientRemark;

  /** 客户端状态 */
  @TableField(value = "client_status")
  private String clientStatus;

  /** 客户端唯一编号 */
  @TableField(value = "client_unique_id")
  private String clientUniqueId;

  /** 创建时间 */
  @TableField(value = "create_time")
  private java.time.LocalDateTime createTime;

  /** 伪装协议 */
  @TableField(value = "disguised_protocol")
  private String disguisedProtocol;

  /** */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /** 修改时间 */
  @TableField(value = "update_time")
  private java.time.LocalDateTime updateTime;

  public JndcClientDTO toDTO() {
    return JndcClientStructMapper.INSTANCE.toDTO(this);
  }

  public JndcClientVO toVO() {
    return JndcClientStructMapper.INSTANCE.toVO(this);
  }
}
