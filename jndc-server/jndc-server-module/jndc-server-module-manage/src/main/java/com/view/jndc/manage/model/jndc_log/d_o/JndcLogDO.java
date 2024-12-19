package com.view.jndc.manage.model.jndc_log.d_o;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.view.jndc.manage.model.jndc_log.JndcLogStructMapper;
import com.view.jndc.manage.model.jndc_log.dto.JndcLogDTO;
import com.view.jndc.manage.model.jndc_log.vo.JndcLogVO;
import lombok.Data;

@TableName("jndc_log")
@Data
public class JndcLogDO {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 日志内容
     */
    @TableField(value = "log_content")
    private String logContent;

    /**
     * 创建日期
     */
    @TableField(value = "log_time")
    private java.time.LocalDateTime logTime;

    /**
     * 日志类型
     */
    @TableField(value = "log_type")
    private String logType;

    /**
     * 来源id
     */
    @TableField(value = "source_id")
    private Long sourceId;

    public JndcLogDTO toDTO() {
        return JndcLogStructMapper.INSTANCE.toDTO(this);
    }

    public JndcLogVO toVO() {
        return JndcLogStructMapper.INSTANCE.toVO(this);
    }
}
