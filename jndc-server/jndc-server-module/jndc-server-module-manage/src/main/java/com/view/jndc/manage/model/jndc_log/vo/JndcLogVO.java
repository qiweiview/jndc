package com.view.jndc.manage.model.jndc_log.vo;

import lombok.Data;

@Data
public class JndcLogVO {

    /**
     * id
     */
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

    private String sourceIdString;


    public void setSourceId(Long sourceId) {
        this.sourceId = sourceId;
        if (sourceId != null && sourceIdString == null) {
            this.sourceIdString = sourceId.toString();
        }
    }

    public void setSourceIdString(String sourceIdString) {
        this.sourceIdString = sourceIdString;
        if (sourceIdString != null) {
            this.sourceId = Long.parseLong(sourceIdString);
        }
    }
}
