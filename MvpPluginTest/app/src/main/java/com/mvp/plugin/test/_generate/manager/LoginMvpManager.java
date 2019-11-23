package com.mvp.plugin.test._generate.manager;

import com.mvp.plugin.test._generate.presenter.ILoginPresenter;
import com.mvp.plugin.test._generate.view.ILoginView;

public class LoginMvpManager {
    private static ILoginView createViewDelegate(Object view) {
        return (ILoginView) java.lang.reflect.Proxy.newProxyInstance(view.getClass().getClassLoader(), new Class[]{ILoginView.class}, new com.mvp.plugin.dependent.delegate.DelegateInvocationHandler(view));
    }

    public static ILoginPresenter createLoginPresenterDelegate(Object view) {
        ILoginView viewDelegate = createViewDelegate(view);
        com.mvp.plugin.test.LoginPresenter presenter = new com.mvp.plugin.test.LoginPresenter(viewDelegate);
        return (ILoginPresenter) java.lang.reflect.Proxy.newProxyInstance(view.getClass().getClassLoader(), new Class[]{ILoginPresenter.class}, new com.mvp.plugin.dependent.delegate.DelegateInvocationHandler(presenter));
    }
}
