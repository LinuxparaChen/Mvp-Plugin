package com.mvp.plugin.dependent.delegate;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class DelegateInvocationHandler implements InvocationHandler {

    private final WeakReference<Object> mWrTarget;

    public DelegateInvocationHandler(Object target) {
        mWrTarget = new WeakReference<>(target);
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (mWrTarget.get() != null) {
            try {
                Method delegateMethod = mWrTarget.get().getClass().getMethod(method.getName(), method.getParameterTypes());
                return delegateMethod.invoke(mWrTarget.get(), args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Exception e = new Exception("DelegateInvocationHandler 的被代理对象被回收了！代理的方法：" + method.getName() + " 参数：" + Arrays.toString(method.getParameterTypes()));
        e.printStackTrace();
        return null;
    }
}
