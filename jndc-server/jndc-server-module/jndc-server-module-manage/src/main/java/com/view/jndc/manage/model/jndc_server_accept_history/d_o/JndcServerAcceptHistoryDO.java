package com.view.jndc.manage.model.jndc_server_accept_history.d_o;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.manage.model.jndc_server_accept_history.JndcServerAcceptHistoryStructMapper;
import com.view.jndc.manage.model.jndc_server_accept_history.dto.JndcServerAcceptHistoryDTO;
import com.view.jndc.manage.model.jndc_server_accept_history.vo.JndcServerAcceptHistoryVO;
import lombok.Data;

@TableName("jndc_server_accept_history")
@Data
public class JndcServerAcceptHistoryDO {
  /** 客户端id */
  @TableField(value = "client_id")
  private String clientId;

  /** 连接时间 */
  @TableField(value = "connect_time")
  private java.time.LocalDateTime connectTime;

  /** 创建时间 */
  @TableField(value = "create_time")
  private java.time.LocalDateTime createTime;

    /**
     * id
     */
  @TableId(value = "id", type = IdType.AUTO)
  private Long id;

  /** 中断时间 */
  @TableField(value = "interrupt_time")
  private java.time.LocalDateTime interruptTime;

    /**
     * 服务id
     */
    @TableField(value = "server_id")
    private Long serverId;

  /** 来源ip */
  @TableField(value = "source_ip")
  private String sourceIp;

  /** 来源端口 */
  @TableField(value = "source_port")
  private Integer sourcePort;

  /** 修改时间 */
  @TableField(value = "update_time")
  private java.time.LocalDateTime updateTime;

  public JndcServerAcceptHistoryDTO toDTO() {
    return JndcServerAcceptHistoryStructMapper.INSTANCE.toDTO(this);
  }

  public JndcServerAcceptHistoryVO toVO() {
    return JndcServerAcceptHistoryStructMapper.INSTANCE.toVO(this);
  }
}
