# Mvp-Plugin
### AS下一款MVP框架代码生成插件(MVP框架经过重新设计)
#### 使用步骤：
1. AS中安装[MVP_Plugin](https://github.com/LinuxparaChen/Mvp-Plugin/releases)插件。
2. 下载[MvpPluginDependent.jar](https://github.com/LinuxparaChen/Mvp-Plugin/releases)并添加到项目的libs目录中。
3. V层指定key和对应P层的类，如下：
```
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
    //MVP_Itr用于标注此方法将会被抽取成接口。
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
```
注：@MVP_V还有一个package是可选属性，用于指定生成文件所在包。默认情况下会在当前文件所在类的包下生成`_generate.manager`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`_generate.presenter`&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`_generate.view`  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;key用于生成类的关键字，生成的类的格式：V层:I{key}View P层:I{key}Presenter 管理类：{key}MvpManager  
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;presenter属性可以指定多个P层类。相应的一个P层类可以被多个V层使用。

4. P层类代码如下：
```
public class LoginPresenter {


    private final ILoginView mLoginView;
    //=====================插件生成代码部分=========================
    public LoginPresenter(ILoginView viewDelegate) {
        mLoginView = viewDelegate;
    }
    //=====================插件生成代码部分=========================

    @ExecuteOn(thread = ThreadMode.ASYNC)
    @MVP_Itr
    public void login(final String name, final String passwd) {
        Log.e("chen_zhanyang", "presenter login: thread-name:" + Thread.currentThread().getName());
        SystemClock.sleep(1000);
        mLoginView.showLoginSuccess(name + " - " + passwd);
    }
}
```
5. @ExecuteOn注解用于指定此方法执行时所在线程，被此注解修饰的方法必须被@MVP_Itr修饰。