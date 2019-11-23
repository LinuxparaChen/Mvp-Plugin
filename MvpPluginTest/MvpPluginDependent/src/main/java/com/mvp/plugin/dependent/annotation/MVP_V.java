package com.mvp.plugin.dependent.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface MVP_V {
    String packaged() default "";
    String key();
    Class<?>[] presenters();
}
