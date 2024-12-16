package com.view.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-09 9:32
 * @description: 密码校验
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "密码格式错误";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
