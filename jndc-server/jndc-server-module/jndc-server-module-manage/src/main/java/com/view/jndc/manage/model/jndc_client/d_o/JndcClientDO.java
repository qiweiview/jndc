package com.view.jndc.manage.model.jndc_client.d_o;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.manage.model.jndc_client.JndcClientStructMapper;
import com.view.jndc.manage.model.jndc_client.dto.JndcClientDTO;
import com.view.jndc.manage.model.jndc_client.vo.JndcClientVO;
import lombok.Data;

@TableName("jndc_client")
@Data
public class JndcClientDO {
    /**
     * 客户端名称
     */
    @TableField(value = "client_name")
    private String clientName;

    /**
     * 客户端备注
     */
    @TableField(value = "client_remark")
    private String clientRemark;

    /**
     * 客户端状态
     */
    @TableField(value = "client_status")
    private String clientStatus;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private java.time.LocalDateTime createTime;

    /**
     * 伪装协议
     */
    @TableField(value = "disguised_protocol")
    private String disguisedProtocol;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 服务主机
     */
    @TableField(value = "server_host")
    private String serverHost;

    /**
     * 服务端口
     */
    @TableField(value = "server_port")
    private Integer serverPort;

    /**
     * 唯一id
     */
    @TableField(value = "unique_id")
    private String uniqueId;

    /**
     * 修改时间
     */
    @TableField(value = "update_time")
    private java.time.LocalDateTime updateTime;

    public JndcClientDTO toDTO() {
        return JndcClientStructMapper.INSTANCE.toDTO(this);
    }

    public JndcClientVO toVO() {
        return JndcClientStructMapper.INSTANCE.toVO(this);
    }
}
