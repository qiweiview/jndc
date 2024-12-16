package com.view.annotation;

import java.lang.annotation.*;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-09-12 9:57
 * @description: 登录
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginLog {
}
