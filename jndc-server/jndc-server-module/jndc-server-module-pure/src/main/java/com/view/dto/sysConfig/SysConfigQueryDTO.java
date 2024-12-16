package com.view.dto.sysConfig;

import com.view.model.dto.BasePageDTO;
import lombok.Data;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-02 15:04
 * @description: 配置查询对象
 */
@Data
public class SysConfigQueryDTO extends BasePageDTO {

    private String configKey;

    private String configName;

    private Long id;

    private String idString;

    public void setId(Long id) {
        this.id = id;
        if (id != null&&idString==null) {
            this.idString = id.toString();
        }
    }

    public void setIdString(String idString) {
        this.idString = idString;
        if (idString != null) {
            this.id = Long.parseLong(idString);
        }
    }
}
