package com.view.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author sjh
 * @version 1.0
 * @date 2024-05-09 9:36
 * @description: 密码格式校验（密码格式应为8-18位数字、字母、符号的任意两种组合）
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final Predicate<String> HAS_DIGIT = s -> s.matches(".*\\d.*");
    private static final Predicate<String> HAS_UPPER_CASE = s -> s.matches(".*[A-Z].*");
    private static final Predicate<String> HAS_LOWER_CASE = s -> s.matches(".*[a-z].*");
    private static final Predicate<String> HAS_SPECIAL_CHAR = s -> s.matches(".*[^A-Za-z0-9].*");

    @Override
    public void initialize(ValidPassword constraintAnnotation) {
    }
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        List<Predicate<String>> cond = Arrays.asList(HAS_DIGIT, HAS_UPPER_CASE, HAS_LOWER_CASE, HAS_SPECIAL_CHAR);
        long count = cond.stream()
                .filter(pred -> pred.test(password))
                .count();
        return password.length() >= 8 && password.length() <= 18 && count >= 2;
    }
}
