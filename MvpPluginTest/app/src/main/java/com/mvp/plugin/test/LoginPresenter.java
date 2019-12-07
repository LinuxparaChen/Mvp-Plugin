package com.mvp.plugin.test;

import android.os.SystemClock;

import android.util.Log;
import com.mvp.plugin.dependent.annotation.ExecuteOn;
import com.mvp.plugin.dependent.annotation.MVP_Itr;
import com.mvp.plugin.dependent.thread.ThreadMode;
import com.mvp.plugin.test._generate.view.ILoginView;

public class LoginPresenter {


    private final ILoginView mLoginView;

    public LoginPresenter(ILoginView viewDelegate) {
        mLoginView = viewDelegate;
    }

    @ExecuteOn(thread = ThreadMode.ASYNC)
    @MVP_Itr
    public void login(final String name, final String passwd) {
        Log.e("chen_zhanyang", "presenter login: thread-name:" + Thread.currentThread().getName());
        SystemClock.sleep(1000);
        mLoginView.showLoginSuccess(name + " - " + passwd);
    }
}
