package com.view.jndc.manage.model.jndc_server.d_o;

import com.view.jndc.manage.model.jndc_server.JndcServerStructMapper;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import com.view.jndc.manage.model.jndc_server.vo.JndcServerVO;
import com.baomidou.mybatisplus.annotation.*;
import java.sql.Timestamp;
import lombok.Data;

@TableName("jndc_server")
@Data
public class JndcServerDO {
  /** jndc-server监听端口 */
  @TableField(value = "bind_port")
  private Integer bindPort;

  /** 绑定策略 */
  @TableField(value = "bind_tactics")
  private String bindTactics;

  /** 创建时间 */
  @TableField(value = "create_time")
  private java.time.LocalDateTime createTime;

  /** */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /** 服务名称 */
  @TableField(value = "server_name")
  private String serverName;

  /** 服务备注 */
  @TableField(value = "server_remark")
  private String serverRemark;

  /** 服务状态 */
  @TableField(value = "server_status")
  private String serverStatus;

  /** 修改时间 */
  @TableField(value = "update_time")
  private java.time.LocalDateTime updateTime;

  public JndcServerDTO toDTO() {
    return JndcServerStructMapper.INSTANCE.toDTO(this);
  }

  public JndcServerVO toVO() {
    return JndcServerStructMapper.INSTANCE.toVO(this);
  }
}
