package com.mvp.plugin.dependent.tools;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodTool {

    public static Object invokeMethod(Method method, Object target, Object[] args) {
        try {
            return method.invoke(target, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            String exception = ExceptionTool.filterException(e);
            throw new RuntimeException(exception.replace("java.lang.RuntimeException:", ""));
        }
        return null;
    }
}
