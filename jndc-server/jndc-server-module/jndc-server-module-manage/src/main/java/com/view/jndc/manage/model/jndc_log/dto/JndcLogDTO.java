package com.view.jndc.manage.model.jndc_log.dto;

import com.view.jndc.manage.model.jndc_log.JndcLogStructMapper;
import com.view.jndc.manage.model.jndc_log.d_o.JndcLogDO;
import com.view.jndc.manage.model.jndc_log.vo.JndcLogVO;
import lombok.Data;

import java.io.Serializable;

@Data
public class JndcLogDTO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
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
     * 日志内容
     */
    private String logContent;

    /**
     * 创建日期
     */
    private java.time.LocalDateTime logTime;

    /**
     * 日志类型
     */
    private String logType;

    /**
     * 来源id
     */
    private Long sourceId;

    /**
     * 来源id
     */
    private String sourceIds;

    /**
     * 一页页的条数
     */
    private Long size;

    /**
     * 当前页码
     */
    protected Long current;

    public JndcLogDO toDO() {
        return JndcLogStructMapper.INSTANCE.toDO(this);
    }

    public JndcLogVO toVO() {
        return JndcLogStructMapper.INSTANCE.toVO(this);
    }
}
