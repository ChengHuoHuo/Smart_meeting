package com.example.yihuier_phone;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.yihuier_phone.User.UserManage;

public class SplashActivity extends AppCompatActivity {
    private static final int GO_HOME = 0;//去主页
    private static final int GO_LOGIN = 1;//去登录页

    /**
     * * 跳转判断
     * @param savedInstanceState
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME://去主页
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    MyApplication app=(MyApplication)getApplication();
                    Log.e("TestDBConn","SplashActivity="+Integer.parseInt(UserManage.getInstance().getUserInfo(SplashActivity.this).getUser_id()));
                    app.setUserid(Integer.parseInt(UserManage.getInstance().getUserInfo(SplashActivity.this).getUser_id()));
                    app.setAccount(UserManage.getInstance().getUserInfo(SplashActivity.this).getUser_account());
                    app.setName(UserManage.getInstance().getUserInfo(SplashActivity.this).getuser_name());
                    startActivity(intent);
                    finish();
                    break;
                case GO_LOGIN://去登录页
                    Intent intent2 = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent2);
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);
        if (UserManage.getInstance().hasUserInfo(this))//自动登录判断，SharePrefences中有数据，则跳转到主页，没数据则跳转到登录页
        {
            mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
        }else {
            mHandler.sendEmptyMessageAtTime(GO_LOGIN, 2000);
        }
    }
}
