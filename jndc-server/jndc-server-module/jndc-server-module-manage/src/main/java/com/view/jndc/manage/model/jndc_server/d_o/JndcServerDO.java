package com.view.jndc.manage.model.jndc_server.d_o;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.manage.model.jndc_server.JndcServerStructMapper;
import com.view.jndc.manage.model.jndc_server.dto.JndcServerDTO;
import com.view.jndc.manage.model.jndc_server.vo.JndcServerVO;
import lombok.Data;

@TableName("jndc_server")
@Data
public class JndcServerDO {
    /**
     * 监听端口
     */
  @TableField(value = "bind_port")
  private Integer bindPort;

  @TableField(value = "bind_host")
  private String bindHost;

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

    /**
     * 唯一id
     */
    @TableField(value = "unique_id")
    private String uniqueId;

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
