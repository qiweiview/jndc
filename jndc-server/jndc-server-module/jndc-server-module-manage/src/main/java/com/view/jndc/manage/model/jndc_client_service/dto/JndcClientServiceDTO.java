package com.view.jndc.manage.model.jndc_client_service.dto;

import com.view.jndc.manage.model.jndc_client_service.JndcClientServiceStructMapper;
import com.view.jndc.manage.model.jndc_client_service.d_o.JndcClientServiceDO;
import com.view.jndc.manage.model.jndc_client_service.vo.JndcClientServiceVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class JndcClientServiceDTO implements Serializable {

  /** 是否自动注册 */
  private Integer autoRegister;

  /** 所属客户端id */
  private Long belongClientId;

  /** 所属客户端id */
  private String belongClientIds;

  /** 客户端唯一id */
  private String clientUniqueId;

  /** 创建时间 */
  private java.time.LocalDateTime createTime;

  /** */
  private Long id;

  /** */
  private String ids;

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

    /**
     * 服务主机
     */
    private String serviceHost;

  /** 服务名称 */
  private String serviceName;

  /** 服务端口 */
  private String servicePort;

  /** 服务状态 */
  private String serviceStatus;

  /** 修改时间 */
  private java.time.LocalDateTime updateTime;

  /** 一页页的条数 */
  private Long size;

  /** 当前页码 */
  protected Long current;

    public JndcClientServiceDO toDO() {
        return JndcClientServiceStructMapper.INSTANCE.toDO(this);
    }

    public JndcClientServiceVO toVO() {
        return JndcClientServiceStructMapper.INSTANCE.toVO(this);
  }
}
