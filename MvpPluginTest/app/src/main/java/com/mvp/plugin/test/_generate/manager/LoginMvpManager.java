package com.mvp.plugin.test._generate.manager;

import com.mvp.plugin.dependent.delegate.PresenterDelegateInvocationHandler;
import com.mvp.plugin.dependent.delegate.ViewDelegateInvocationHandler;
import com.mvp.plugin.test.LoginPresenter;

import java.lang.reflect.Proxy;

public class LoginMvpManager {
    public static com.mvp.plugin.test._generate.view.ILoginView createViewDelegate(Object view) {
        return (com.mvp.plugin.test._generate.view.ILoginView) Proxy.newProxyInstance(view.getClass().getClassLoader(), new Class[]{com.mvp.plugin.test._generate.view.ILoginView.class}, new ViewDelegateInvocationHandler(view));
    }

    public static com.mvp.plugin.test._generate.presenter.ILoginPresenter createLoginPresenterDelegate(Object view) {
        com.mvp.plugin.test._generate.view.ILoginView viewDelegate = createViewDelegate(view);
        LoginPresenter presenter = new LoginPresenter(viewDelegate);
        return (com.mvp.plugin.test._generate.presenter.ILoginPresenter) Proxy.newProxyInstance(view.getClass().getClassLoader(), new Class[]{com.mvp.plugin.test._generate.presenter.ILoginPresenter.class}, new PresenterDelegateInvocationHandler(presenter));
    }
}
