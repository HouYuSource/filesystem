package cn.shaines.filesystem.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 要求防盗链等校验
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ChainRequired {

    // 代表是否需要检查
    Type value() default Type.CHECK;

    enum Type {
        // 需要检查
        CHECK,
        // 直接放行
        PASS
    }
}
