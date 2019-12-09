package com.mvp.plugin.dependent.delegate;

import com.mvp.plugin.dependent.annotation.ExecuteOn;
import com.mvp.plugin.dependent.tools.MethodTool;
import com.mvp.plugin.dependent.tools.ThreadTool;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class PresenterDelegateInvocationHandler implements InvocationHandler {

    private final Object mTarget;

    public PresenterDelegateInvocationHandler(Object target) {
        mTarget = target;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        try {
            Method delegateMethod = mTarget.getClass().getMethod(method.getName(), method.getParameterTypes());
            delegateMethod.setAccessible(true);
            if (!needThreadHandle(delegateMethod,args)) {
                return MethodTool.invokeMethod(delegateMethod, mTarget, args);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean needThreadHandle(Method method,Object[] args){
        ExecuteOn executeOnAnno = method.getAnnotation(ExecuteOn.class);
        if (executeOnAnno != null) {
            if (method.getReturnType() != Void.TYPE) {
                throw new RuntimeException("ExecuteOn注解修饰的函数" + method.getName() + "返回值必须是void！");
            }
            switch (executeOnAnno.thread()) {
                case MAIN:
                    ThreadTool.executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            MethodTool.invokeMethod(method, mTarget, args);
                        }
                    });
                    return true;
                case ASYNC:
                    ThreadTool.executeOnAsyncThread(new Runnable() {
                        @Override
                        public void run() {
                            MethodTool.invokeMethod(method, mTarget, args);
                        }
                    });
                    return true;
            }
        }
        return false;
    }

}
