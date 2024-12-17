package com.view.jndc.manage.model.jndc_server.dto;

import com.view.jndc.manage.model.jndc_server.JndcServerStructMapper;
import com.view.jndc.manage.model.jndc_server.d_o.JndcServerDO;
import com.view.jndc.manage.model.jndc_server.vo.JndcServerVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class JndcServerDTO implements Serializable {

  /** jndc-server监听端口 */
  private Integer bindPort;

  /** 绑定策略 */
  private String bindTactics;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** */
  private Long id;

  private String idString;

  public void setId(Long id) {
    this.id = id;
    if (id != null && idString == null) {
      this.idString = id.toString();
    }
  }

  public void setIdString(String idString) {
    this.idString = idString;
    if (idString != null) {
      this.id = Long.parseLong(idString);
    }
  }

  /** */
  private String ids;

  /** 服务名称 */
  private String serverName;

  /** 服务备注 */
  private String serverRemark;

  /** 服务状态 */
  private String serverStatus;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;

  /** 一页页的条数 */
  private Long size;

  /** 当前页码 */
  protected Long current;

  public JndcServerDO toDO() {
    return JndcServerStructMapper.INSTANCE.toDO(this);
  }

  public JndcServerVO toVO() {
    return JndcServerStructMapper.INSTANCE.toVO(this);
  }
}
