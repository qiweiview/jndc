package com.view.validator;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-07-02 15:07
 * @description: 验证某个整数值是否在指定的枚举类型中
 */

public class EnumValueValidator implements ConstraintValidator<EnumValue, Integer> {

    private EnumValue annotation;

    @Override
    public void initialize(EnumValue annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        Object[] enumValues = this.annotation.enumClass().getEnumConstants();
        for (Object enumValue : enumValues) {
            if (value.equals(((Enum<?>) enumValue).ordinal())) {
                return true;
            }
        }
        return false;
    }
}
