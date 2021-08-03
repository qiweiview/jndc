package jndc.http_support;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface WebMapping {
    enum RESPONSE_TYPE {
        JSON,
        HTML,
        TEXT
    }

    public String value() default "";

    public RESPONSE_TYPE type() default RESPONSE_TYPE.JSON;
}
