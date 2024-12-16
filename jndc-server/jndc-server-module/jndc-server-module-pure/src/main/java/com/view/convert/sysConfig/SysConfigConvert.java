package com.view.convert.sysConfig;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.view.dao.entity.SysConfig;
import com.view.dto.sysConfig.SysConfigCreateDTO;
import com.view.dto.sysConfig.SysConfigUpdateDTO;
import com.view.vo.sysConfig.SysConfigSimpleVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-08-02 15:14
 * @description: 配置转换
 */
@Mapper
public interface SysConfigConvert {

    SysConfigConvert INSTANCE = Mappers.getMapper(SysConfigConvert.class);

    Page<SysConfigSimpleVO> convertSimplePage(Page<SysConfig> page);

    SysConfig convertCreateDTO(SysConfigCreateDTO createDTO);

    SysConfig convertUpdateDTO(SysConfigUpdateDTO updateDTO);
}
