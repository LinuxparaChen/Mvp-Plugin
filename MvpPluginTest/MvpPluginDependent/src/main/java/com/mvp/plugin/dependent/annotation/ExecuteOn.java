package com.mvp.plugin.dependent.annotation;

import com.mvp.plugin.dependent.thread.ThreadMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExecuteOn {
    ThreadMode thread();
}
