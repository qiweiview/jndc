package com.view.annotation;

import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-04-29 21:13
 * @description: 后台前缀
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
public @interface AdminPrefix {
}
