package com.view.jndc.manage.model.jndc_server_app.dto;

import com.view.jndc.manage.model.jndc_server_app.JndcServerAppStructMapper;
import com.view.jndc.manage.model.jndc_server_app.d_o.JndcServerAppDO;
import com.view.jndc.manage.model.jndc_server_app.vo.JndcServerAppVO;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

@Data
public class JndcServerAppDTO implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(JndcServerAppDTO.class);
    /**
     * 监听域名
     */
    private String bindHost;

    /**
     * 监听端口
     */
    private Integer bindPort;

    /**
     * 监听状态
     */
    private String bindStatus;

    private String bindType;

    private String metaData;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * id
     */
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

    /**
     * id
     */
    private String ids;

    /**
     * jndc服务id
     */
    private Long serverId;

    private String serverIdString;

    public void setServerId(Long serverId) {
        this.serverId = serverId;
        if (serverId != null && serverIdString == null) {
            this.serverIdString = serverId.toString();
        }
    }

    public void setServerIdString(String serverIdString) {
        this.serverIdString = serverIdString;
        if (serverIdString != null) {
            try {
                this.serverId = Long.parseLong(serverIdString);
            } catch (NumberFormatException e) {
                log.warn("类型转换失败{}", serverIdString);
            }
        }
    }

    /**
     * jndc服务id
     */
    private String serverIds;

    /**
     * 来源客户端
     */
    private String sourceClientId;

    /**
     * 来源服务
     */
    private String sourceServiceId;

    /**
     * 一页页的条数
     */
    private Long size;

    /**
     * 当前页码
     */
    protected Long current;

    public JndcServerAppDO toDO() {
        return JndcServerAppStructMapper.INSTANCE.toDO(this);
    }

    public JndcServerAppVO toVO() {
        return JndcServerAppStructMapper.INSTANCE.toVO(this);
    }
}
