package com.view.jndc.manage.model.jndc_server_app_bind.vo;

import lombok.Data;

@Data
public class JndcServerAppBindVO {

  /** 绑定端口 */
  private Integer bindPort;

  /** 绑定来源 */
  private String bindSource;

  /** 绑定状态 */
  private String bindStatus;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** */
  private Long id;

    /**
     * 字符id（处理浏览器long精度丢失问题）
     */
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

  /** 最后绑定结果 */
  private String latestBindResult;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;
}
