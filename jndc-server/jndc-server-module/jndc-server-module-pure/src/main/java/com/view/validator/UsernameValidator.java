package com.view.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-07 17:03
 * @description: 自定义登录参数校验
 */
public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    /**
     * 定义电子邮件的正则表达式
     */
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    /**
     * 初始化方法，此处我们不需要初始化数据，所以留空即可
     */
    @Override
    public void initialize(ValidUsername constraintAnnotation) {
    }

    /**
     * 实现isValid方法，定义校验逻辑
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 输入值为null时直接返回false
        if (value == null ) {
            return false;
        }

        // 如果是电子邮件或具有有效前缀则返回true，否则返回false
        return Pattern.compile(EMAIL_PATTERN).matcher(value).matches();
    }
}
