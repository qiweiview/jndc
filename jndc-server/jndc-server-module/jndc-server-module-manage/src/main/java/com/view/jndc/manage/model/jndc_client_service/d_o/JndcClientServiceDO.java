package com.view.jndc.manage.model.jndc_client_service.d_o;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.manage.model.jndc_client_service.JndcClientServiceStructMapper;
import com.view.jndc.manage.model.jndc_client_service.dto.JndcClientServiceDTO;
import com.view.jndc.manage.model.jndc_client_service.vo.JndcClientServiceVO;
import lombok.Data;

@TableName("jndc_client_service")
@Data
public class JndcClientServiceDO {
    /**
     * 是否自动注册
     */
    @TableField(value = "auto_register")
    private Integer autoRegister;

    /**
     * 所属客户端id
     */
    @TableField(value = "client_id")
    private Long clientId;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private java.time.LocalDateTime createTime;

    /**
     * 期望端口
     */
    @TableField(value = "expect_port")
    private Integer expectPort;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 服务主机
     */
    @TableField(value = "service_host")
    private String serviceHost;

    /**
     * 服务模式
     */
    @TableField(value = "service_mode")
    private String serviceMode;

    /**
     * 服务名称
     */
    @TableField(value = "service_name")
    private String serviceName;

    /**
     * 服务端口
     */
    @TableField(value = "service_port")
    private Integer servicePort;

    /**
     * 服务协议
     */
    @TableField(value = "service_protocol")
    private String serviceProtocol;

    /**
     * 服务状态
     */
    @TableField(value = "service_status")
    private String serviceStatus;

    /**
     * 服务唯一id
     */
    @TableField(value = "service_unique_id")
    private String serviceUniqueId;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private java.time.LocalDateTime updateTime;

    public JndcClientServiceDTO toDTO() {
        return JndcClientServiceStructMapper.INSTANCE.toDTO(this);
    }

    public JndcClientServiceVO toVO() {
        return JndcClientServiceStructMapper.INSTANCE.toVO(this);
    }

    public boolean checkAutoRegister() {
        return autoRegister != null && autoRegister == 1;
    }
}
