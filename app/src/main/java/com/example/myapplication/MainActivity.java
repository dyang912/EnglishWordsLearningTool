package com.example.myapplication;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapplication.myfragment.InfoFragment;
import com.example.myapplication.myfragment.LoginFragment;
import com.example.myapplication.myfragment.RegisterFragment;
import com.example.myapplication.myfragment.SearchFragment;
import com.example.myapplication.myfragment.WordbookFragment;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.OnClick;

public class MainActivity extends FragmentActivity {
    private FragmentManager mFragmentManager;
    private LoginFragment loginFragment = new LoginFragment();
    private RegisterFragment registerFragment = new RegisterFragment();
    private InfoFragment infoFragment = new InfoFragment();
    private SearchFragment searchFragment = new SearchFragment();
    private WordbookFragment wordbookFragment = new WordbookFragment();

    public static String realCode;
    public static User mUser = null;
    private DBOperator mDBOperator;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_search:
                    fragmentTransaction.replace(R.id.fragment_view, searchFragment, "search").commit();
                    return true;
                case R.id.navigation_wordbook:
                    fragmentTransaction.replace(R.id.fragment_view, wordbookFragment, "wordbook").commit();
                    return true;
                case R.id.navigation_user:
                    if(mUser != null)
                        fragmentTransaction.replace(R.id.fragment_view, infoFragment, "info").commit();
                    else
                        fragmentTransaction.replace(R.id.fragment_view, loginFragment, "login").commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        mDBOperator = new DBOperator(this);

        fragmentTransaction.replace(R.id.fragment_view, searchFragment, "search").commit();
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }


    @OnClick({
            R.id.tv_loginactivity_register,
            R.id.bt_loginactivity_login,
            R.id.iv_registeractivity_back,
            R.id.iv_registeractivity_showCode,
            R.id.bt_registeractivity_register,
            R.id.bt_user_logout,
    })

    public void onClick(View view) {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        EditText temp;
        switch (view.getId()) {
            case R.id.tv_loginactivity_register:
                fragmentTransaction.replace(R.id.fragment_view, registerFragment, "register").commit();
                break;

            case R.id.bt_loginactivity_login:
                temp = findViewById(R.id.et_loginactivity_username);
                String name = temp.getText().toString().trim();
                temp = findViewById(R.id.et_loginactivity_password);
                String password = temp.getText().toString().trim();

                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password)) {
                    ArrayList<User> data = mDBOperator.getAllData();
                    boolean match = false;
                    for (int i = 0; i < data.size(); i++) {
                        User user = data.get(i);
                        if (name.equals(user.getName()) && password.equals(user.getPassword())) {
                            match = true;
                            mUser = user;
                            break;
                        } else {
                            match = false;
                        }
                    }
                    if (match) {
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                        fragmentTransaction.replace(R.id.fragment_view, infoFragment, "info").commit();
                    } else {
                        Toast.makeText(this, "用户名或密码不正确，请重新输入", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "请输入你的用户名或密码", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.iv_registeractivity_back:         //返回登录页面
                fragmentTransaction.replace(R.id.fragment_view, loginFragment, "login").commit();
                break;

            case R.id.bt_user_logout:                   //返回登录页面
                fragmentTransaction.replace(R.id.fragment_view, loginFragment, "login").commit();
                mUser = null;
                break;

            case R.id.iv_registeractivity_showCode:     //改变随机验证码的生成
                ImageView imageView = findViewById(R.id.iv_registeractivity_showCode);
                imageView.setImageBitmap(Code.getInstance().createBitmap());
                realCode = Code.getInstance().getCode().toLowerCase();
                break;

            case R.id.bt_registeractivity_register:     //注册按钮
                //获取用户输入的用户名、密码、验证码
                temp = findViewById(R.id.et_registeractivity_username);
                String rusername = temp.getText().toString().trim();
                temp = findViewById(R.id.et_registeractivity_password1);
                String rpassword1 = temp.getText().toString().trim();
                temp = findViewById(R.id.et_registeractivity_password2);
                String rpassword2 = temp.getText().toString().trim();
                temp = findViewById(R.id.et_registeractivity_captcha);
                String capCode = temp.getText().toString().trim();

                //注册验证
                if (!TextUtils.isEmpty(rusername) && !TextUtils.isEmpty(rpassword1) &&
                    !TextUtils.isEmpty(rpassword2) && !TextUtils.isEmpty(capCode)) {
                    if(!rpassword1.equals(rpassword2)) {
                        Toast.makeText(this, "两次输入密码不符", Toast.LENGTH_SHORT).show();
                    }
                    else if (capCode.equals(realCode)) {
                        //将用户名和密码加入到数据库中
                        mDBOperator.add(rusername, rpassword1);
                        mUser = new User(rusername, rpassword1);
                        fragmentTransaction.replace(R.id.fragment_view, infoFragment, "info").commit();
                        Toast.makeText(this, "验证通过，注册成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "验证码错误,注册失败", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "未完善信息，注册失败", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void changeLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getApplicationContext().getResources().updateConfiguration(configuration, getApplicationContext().getResources().getDisplayMetrics());
    }
}
