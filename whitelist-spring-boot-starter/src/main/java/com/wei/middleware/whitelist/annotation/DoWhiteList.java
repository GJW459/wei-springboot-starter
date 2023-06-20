package com.wei.middleware.whitelist.annotation;

import java.lang.annotation.*;

/**
 * @author wei
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface DoWhiteList {

    String key() default "";

    String returnJson() default "";
}
