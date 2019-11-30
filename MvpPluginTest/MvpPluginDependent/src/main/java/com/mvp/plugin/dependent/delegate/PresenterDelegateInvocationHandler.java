package com.mvp.plugin.dependent.delegate;

import java.lang.reflect.InvocationHandler;
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
            return delegateMethod.invoke(mTarget, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
