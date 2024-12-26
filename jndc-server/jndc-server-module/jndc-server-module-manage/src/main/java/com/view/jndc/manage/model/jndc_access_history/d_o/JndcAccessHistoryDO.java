package com.view.jndc.manage.model.jndc_access_history.d_o;

import com.view.jndc.manage.model.jndc_access_history.JndcAccessHistoryStructMapper;
import com.view.jndc.manage.model.jndc_access_history.dto.JndcAccessHistoryDTO;
import com.view.jndc.manage.model.jndc_access_history.vo.JndcAccessHistoryVO;
import com.baomidou.mybatisplus.annotation.*;
import java.sql.Timestamp;
import lombok.Data;

@TableName("jndc_access_history")
@Data
public class JndcAccessHistoryDO {
  /** 创建时间 */
  @TableField(value = "create_time")
  private java.time.LocalDateTime createTime;

  /** 访问目标 */
  @TableField(value = "destination")
  private String destination;

  /** 目标id */
  @TableField(value = "destination_id")
  private Long destinationId;

  /** id */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /** 数据采样 */
  @TableField(value = "package_sampling")
  private String packpacjageSampling;

  /** ip地址 */
  @TableField(value = "remote_ip")
  private String remoteIp;

  /** 端口 */
  @TableField(value = "remote_port")
  private Integer remotePort;

  public JndcAccessHistoryDTO toDTO() {
    return JndcAccessHistoryStructMapper.INSTANCE.toDTO(this);
  }

  public JndcAccessHistoryVO toVO() {
    return JndcAccessHistoryStructMapper.INSTANCE.toVO(this);
  }
}
