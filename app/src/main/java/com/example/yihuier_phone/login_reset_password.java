package com.example.yihuier_phone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class login_reset_password extends AppCompatActivity {

    EditText et;
    private Connection conn=null;
    String u_account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_reset_password);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);
        et=(EditText)findViewById(R.id.et_reset_account);


        Button btn=(Button)findViewById(R.id.confirm);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                u_account=et.getText().toString();
                if(u_account!=null){
                    gotocheckaccount(u_account);
                }
                else
                {
                    Toast.makeText(login_reset_password.this,"账号不为空",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    public  void gotocheckaccount(final String s){
        final MyApplication app=(MyApplication)getApplication();
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
                    String sql = "SELECT * FROM bz_user where user_account=?" ;
                    try {
                        // 创建用来执行sql语句的对象
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setString(1,s);
                        // 执行sql查询语句并获取查询信息
                        ResultSet rSet = preparedStatement.executeQuery();
                        while (rSet.next()){
                            if(rSet.getString("user_account").equals(s)){
                                app.setAccount(rSet.getString("user_account"));
                                Intent intent = new Intent(login_reset_password.this, resetpassword.class);
                                startActivity(intent);
                                finish();
                                break;
                            }
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
}
