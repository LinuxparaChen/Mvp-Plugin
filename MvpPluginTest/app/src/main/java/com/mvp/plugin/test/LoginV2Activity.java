package com.mvp.plugin.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.mvp.plugin.dependent.annotation.MVP_Itr;
import com.mvp.plugin.dependent.annotation.MVP_V;


@MVP_V(key = "Login",presenters = {LoginPresenter.class})
public class LoginV2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_v2);
    }

    @MVP_Itr
    public void showLoginSuccess(String name) {
        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_LONG).show();
    }

    @MVP_Itr
    public void showLoginFailed(String errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
