package com.example.yihuier_phone;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.example.yihuier_phone.User.UserManage;
import com.example.yihuier_phone.personalinfo.PersonalMsgActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity{
    private final static int CONNECT_OUT_TIME = 5000;
    private EditText et_account;
    private EditText et_password;
    private MD5Utils md5Utils = new MD5Utils();
    private String account;
    private String login_password;
    private CheckBox cbRemember;
    private Connection conn=null;
    private Boolean isRemember=false;
    private final String PREFERENCES_NAME="userinfo";
    private String userName,passWord;
    private String user_id;
    private String user_account;
    private String user_name;

    private String See_user_status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("一会儿");
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);

        et_account=(EditText)findViewById(R.id.et_login_account);
        et_password=(EditText)findViewById(R.id.et_login_password);
        cbRemember=(CheckBox)findViewById(R.id.savePasswordCB);

        final Button btn_login=(Button)findViewById(R.id.login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection();
            }
        });

        final Button btn_forget=(Button)findViewById(R.id.login_error);
        btn_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset_password();
            }
        });

        cbRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRemember=isChecked;
            }
        });
        SharedPreferences preferences=getSharedPreferences(PREFERENCES_NAME,Activity.MODE_PRIVATE);

        et_account.setText(preferences.getString("ReUsername",null));
        cbRemember.setChecked(preferences.getBoolean("Remember",true));
        if(cbRemember.isChecked()){
            et_password.setText(preferences.getString("RePassWord",null));
        }else {
            et_password.setText(null);
        }
    }


    public void connection(){
        final MyApplication app=(MyApplication)getApplication();
        account=et_account.getText().toString();
        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                //设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                String ip = "47.96.104.134";
                int port = 3306;
                String dbName = "bz_meeting";
                String url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName; // 构建连接mysql的字符串
                String user = "root";
                String password = "Sayello.123";

                //连接JDBC
                try {
                    conn = DriverManager.getConnection(url, user, password);
                    Log.e("TestDBConn", "远程连接成功！");
                } catch (SQLException e) {
                    Log.e("TestDBConn", "远程连接失败！");
                }

                if (conn != null) {
                    String sql = "SELECT * FROM bz_user where user_name = ? or user_account=?" ;
                    try {
                        // 创建用来执行sql语句的对象
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);

                        preparedStatement.setString(1,account);
                        preparedStatement.setString(2,account);
                        // 执行sql查询语句并获取查询信息
                        ResultSet rSet = preparedStatement.executeQuery();
                        while (rSet.next()){
                            Log.e("TestDBConn", rSet.getString("user_password"));
                            if(rSet.getString("user_name").equals(account)||rSet.getString("user_account").equals(account))
                            {
                                user_id= String.valueOf(rSet.getInt("user_id"));
                                user_account=rSet.getString("user_account");
                                user_name=rSet.getString("user_name");
                                app.setUserid(rSet.getInt("user_id"));
                                app.setAccount(rSet.getString("user_account"));
                                app.setName(rSet.getString("user_name"));
                                GetUserStatus(user_id);
                                LoginAndChecked(rSet.getString("user_password"));
                                break;}
                        }
                        rSet.close();
                        preparedStatement.close();
                        // Log.i("TestDBConn",rSet.getString("ac_name"));
                    } catch (SQLException e) {
                        Log.e("TestDBConn", "createStatement error");
                    }

                    try {
                        conn.close();
                    } catch (SQLException e1) {
                        Log.e("TestDBConn", "关闭连接失败");
                    }
                }

            }
        });
        thread.start();
    }
    public void GetUserStatus(final String g){
         final String url="http://39.108.10.155:8080/UserManager/Users/userStatus.json";
        Thread thread=new Thread(){
            @Override
            public void run() {
                try {
                    // 第一步：创建必要的URL对象
                    URL httpUrl = new URL(url);
                    // 第二步：根据URL对象，获取HttpURLConnection对象
                    HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
                    // 第三步：为HttpURLConnection对象设置必要的参数（是否允许输入数据、连接超时时间、请求方式）
                    connection.setConnectTimeout(CONNECT_OUT_TIME);
                    connection.setReadTimeout(CONNECT_OUT_TIME);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    String content = "user_id=" + URLEncoder.encode(String.valueOf(g));// 无论服务器转码与否，这里不需要转码，因为Android系统自动已经转码为utf-8啦
                    //添加post请求的两行属性
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", content.length() + "");
                    // 第四步：向服务器写入数据
                    OutputStream out = connection.getOutputStream();
                    out.write(content.getBytes());
                    out.flush();
                    out.close();
                    // 第五步：开始读取服务器返回数据

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    final StringBuffer buffer = new StringBuffer();
                    String str1 = null;
                    while ((str1 = reader.readLine()) != null) {
                        buffer.append(str1);
                    }
                    reader.close();
                    Log.e("TestDBConn",buffer.toString());
                    JSONObject obj = new JSONObject( buffer.toString());
                    String User_status=obj.getString("data");
                    Log.e("TestDBConn","data="+User_status);
                    See_user_status=User_status;
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void LoginAndChecked(String p){
        login_password=et_password.getText().toString();
        String pwd=md5Utils.toMd5(login_password);
        if(pwd.equals(p)){
            UserManage.getInstance().saveUserInfo(LoginActivity.this, account, login_password,user_id,user_account,user_name);
            if(See_user_status.equals("1")){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                Looper.prepare();
                Toast.makeText(this,"登录成功",Toast.LENGTH_LONG).show();
                Looper.loop();
            }
            else if(See_user_status.equals("4")){
                Intent intent = new Intent(this, PersonalMsgActivity.class);
                startActivity(intent);
                finish();
                Looper.prepare();
                Toast.makeText(this,"登录成功",Toast.LENGTH_LONG).show();
                Looper.loop();
            }
            else {
                Looper.prepare();
                Toast.makeText(this,"该用户被注销",Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }
        else if(account==null){
            Looper.prepare();
            Toast.makeText(this, "不存在该用户！", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
        else{
            Looper.prepare();
            Toast.makeText(this,"密码错误，请输入正确的密码！",Toast.LENGTH_LONG).show();
            Looper.loop();
        }
    }

    public void reset_password(){
        Intent intent=new Intent(this,login_reset_password.class);
        startActivity(intent);
    }

    public void onStop() {
        super.onStop();
        SharedPreferences agPreferences=getSharedPreferences(PREFERENCES_NAME,Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=agPreferences.edit();
        userName=et_account.getText().toString();
        passWord=et_password.getText().toString();
        editor.putString("ReUsername",userName);
        editor.putString("RePassWord",passWord);
        editor.putBoolean("Remember",isRemember);
        editor.commit();
    }
}