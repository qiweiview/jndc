package com.view.jndc.manage.model.jndc_access_history.dto;

import com.view.jndc.manage.model.jndc_access_history.JndcAccessHistoryStructMapper;
import com.view.jndc.manage.model.jndc_access_history.d_o.JndcAccessHistoryDO;
import com.view.jndc.manage.model.jndc_access_history.vo.JndcAccessHistoryVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class JndcAccessHistoryDTO implements Serializable {

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** 访问目标 */
  private String destination;

  /** 目标id */
  private Long destinationId;

  /** 目标id */
  private String destinationIds;

  /** id */
  private Long id;

  /** id */
  private String ids;

  /** 字符id（处理浏览器long精度丢失问题） */
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

  /** 数据采样 */
  private String packageSampling;

  /** ip地址 */
  private String remoteIp;

  /** 端口 */
  private Integer remotePort;

  /** 一页页的条数 */
  private Long size;

  /** 当前页码 */
  protected Long current;

  public JndcAccessHistoryDO toDO() {
    return JndcAccessHistoryStructMapper.INSTANCE.toDO(this);
  }

  public JndcAccessHistoryVO toVO() {
    return JndcAccessHistoryStructMapper.INSTANCE.toVO(this);
  }
}
