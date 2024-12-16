package com.view.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-07 17:04
 * @description: 自定义登录参数校验
 */
@Documented
@Constraint(validatedBy = UsernameValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUsername {

    /**
     * 校验失败时的默认消息
     */
    String message() default "参数格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
