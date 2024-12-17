package com.view.jndc.manage.model.jndc_server_app_bind.d_o;

import com.view.jndc.manage.model.jndc_server_app_bind.JndcServerAppBindStructMapper;
import com.view.jndc.manage.model.jndc_server_app_bind.dto.JndcServerAppBindDTO;
import com.view.jndc.manage.model.jndc_server_app_bind.vo.JndcServerAppBindVO;
import com.baomidou.mybatisplus.annotation.*;
import java.sql.Timestamp;
import lombok.Data;

@TableName("jndc_server_app_bind")
@Data
public class JndcServerAppBindDO {
  /** 绑定端口 */
  @TableField(value = "bind_port")
  private Integer bindPort;

  /** 绑定来源 */
  @TableField(value = "bind_source")
  private String bindSource;

  /** 绑定状态 */
  @TableField(value = "bind_status")
  private String bindStatus;

  /** 创建时间 */
  @TableField(value = "create_time")
  private java.time.LocalDateTime createTime;

  /** */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /** 最后绑定结果 */
  @TableField(value = "latest_bind_result")
  private String latestBindResult;

  /** 修改时间 */
  @TableField(value = "update_time")
  private java.time.LocalDateTime updateTime;

  public JndcServerAppBindDTO toDTO() {
    return JndcServerAppBindStructMapper.INSTANCE.toDTO(this);
  }

  public JndcServerAppBindVO toVO() {
    return JndcServerAppBindStructMapper.INSTANCE.toVO(this);
  }
}
