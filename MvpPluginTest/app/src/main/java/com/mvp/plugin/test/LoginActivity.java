package com.mvp.plugin.test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mvp.plugin.dependent.annotation.MVP_Itr;
import com.mvp.plugin.dependent.annotation.MVP_V;
import com.mvp.plugin.test._generate.manager.LoginMvpManager;
import com.mvp.plugin.test._generate.presenter.ILoginPresenter;


@MVP_V(key = "Login",presenters = {LoginPresenter.class})
public class LoginActivity extends AppCompatActivity {


    private ILoginPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        mPresenter = LoginMvpManager.createLoginPresenterDelegate(this);
        mPresenter.login("Test","123456");
    }

    @MVP_Itr
    public void updateUiWithUser(String name) {
        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
    }
    @MVP_Itr
    public void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
