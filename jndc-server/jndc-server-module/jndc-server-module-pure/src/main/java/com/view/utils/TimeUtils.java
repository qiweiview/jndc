package com.view.utils;

import com.view.enums.ZoneEnum;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-09 23:04
 * @description: 时间工具类
 */
public class TimeUtils {

    private TimeUtils(){
    }

    public static LocalDateTime now(){
        return LocalDateTime.now(ZoneId.of(ZoneEnum.SHANGHAI.getZone()));
    }
}
