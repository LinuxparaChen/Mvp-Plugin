package com.mvp.plugin.test;

import com.mvp.plugin.dependent.annotation.MVP_Itr;
import com.mvp.plugin.test._generate.view.ILoginView;

public class LoginPresenter {


    private final ILoginView mLoginView;

    public LoginPresenter(ILoginView viewDelegate) {
        mLoginView = viewDelegate;
    }

    @MVP_Itr
    public void login(String name, String passwd){
        mLoginView.showLoginSuccess(name+" - "+passwd);
    }
}
