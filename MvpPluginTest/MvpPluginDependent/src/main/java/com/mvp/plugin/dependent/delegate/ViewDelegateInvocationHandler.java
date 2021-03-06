package com.mvp.plugin.dependent.delegate;

import com.mvp.plugin.dependent.annotation.ExecuteOn;
import com.mvp.plugin.dependent.tools.MethodTool;
import com.mvp.plugin.dependent.tools.ThreadTool;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ViewDelegateInvocationHandler implements InvocationHandler {

    private final WeakReference<Object> mWrTarget;

    public ViewDelegateInvocationHandler(Object target) {
        mWrTarget = new WeakReference<>(target);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (mWrTarget.get() != null) {
            try {
                //被代理的方法
                Method delegateMethod = mWrTarget.get().getClass().getMethod(method.getName(), method.getParameterTypes());
                delegateMethod.setAccessible(true);
                if (!needThreadHandle(delegateMethod,args)) {
                    return MethodTool.invokeMethod(delegateMethod, mWrTarget.get(), args);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        } else {
            Exception e = new Exception("View的被代理对象被回收了！代理的方法：" + method.getName() + " 参数：" + Arrays.toString(method.getParameterTypes()));
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
                            MethodTool.invokeMethod(method, mWrTarget.get(), args);
                        }
                    });
                    return true;
                case ASYNC:
                    ThreadTool.executeOnAsyncThread(new Runnable() {
                        @Override
                        public void run() {
                            MethodTool.invokeMethod(method, mWrTarget.get(), args);
                        }
                    });
                    return true;
            }
        }
        return false;
    }




}
