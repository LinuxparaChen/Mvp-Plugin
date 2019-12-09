package com.mvp.plugin.test;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mvp.plugin.dependent.annotation.ExecuteOn;
import com.mvp.plugin.dependent.annotation.MVP_Itr;
import com.mvp.plugin.dependent.annotation.MVP_V;
import com.mvp.plugin.dependent.thread.ThreadMode;
import com.mvp.plugin.test._generate.manager.LoginMvpManager;
import com.mvp.plugin.test._generate.presenter.ILoginPresenter;


@MVP_V(key = "Login", presenters = {LoginPresenter.class})
public class LoginActivity extends AppCompatActivity {


    private ILoginPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mPresenter = LoginMvpManager.createLoginPresenterDelegate(this);
        mPresenter.login("Test", "123456");
    }

//    @ExecuteOn(thread = ThreadMode.MAIN)
    @MVP_Itr
    public void showLoginSuccess(String name) {
        Log.e("chen_zhanyang", "view showLoginSuccess: thread-name:" + Thread.currentThread().getName());
        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
    }

    @MVP_Itr
    public void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
