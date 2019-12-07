package com.mvp.plugin.test._generate.manager;

import com.mvp.plugin.dependent.delegate.DelegateInvocationHandler;
import com.mvp.plugin.test.LoginPresenter;
import com.mvp.plugin.test._generate.presenter.ILoginPresenter;
import com.mvp.plugin.test._generate.view.ILoginView;

import java.lang.reflect.Proxy;

public class LoginMvpManager {
    private static ILoginView createViewDelegate(Object view) {
        return (ILoginView) Proxy.newProxyInstance(view.getClass().getClassLoader(), new Class[]{ILoginView.class}, new DelegateInvocationHandler(view));
    }

    public static ILoginPresenter createLoginPresenterDelegate(Object view) {
        ILoginView viewDelegate = createViewDelegate(view);
        LoginPresenter presenter = new LoginPresenter(viewDelegate);
        return (ILoginPresenter) Proxy.newProxyInstance(view.getClass().getClassLoader(), new Class[]{ILoginPresenter.class}, new DelegateInvocationHandler(presenter));
    }
}
