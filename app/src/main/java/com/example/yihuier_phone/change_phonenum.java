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

public class change_phonenum extends AppCompatActivity {
    EditText et_phone;
    private Connection conn=null;
    String u_account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);
        final MyApplication app=(MyApplication)getApplication();
        u_account=app.getAccount();

        et_phone=(EditText)findViewById(R.id.et_new_phonenum);
        Button btn_changephonenum=(Button)findViewById(R.id.confirm);
        btn_changephonenum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String p=et_phone.getText().toString();
                if(p.length()!=11){
                    Toast.makeText(change_phonenum.this,"请重新输入手机号",Toast.LENGTH_LONG).show();
                }
                else{

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
                                String sql ="update bz_user set user_phone=? where user_account=? ";
                                try {
                                    // 创建用来执行sql语句的对象
                                    PreparedStatement preparedStatement = conn.prepareStatement(sql);
                                    preparedStatement.setString(1, p);
                                    Log.e("TestDBConn", "kkkkk+"+p);
                                    preparedStatement.setString(2,u_account);
                                    Log.e("TestDBConn", "ssss+"+u_account);
                                    // 执行sql查询语句并获取查询信息
                                    int rSet = preparedStatement.executeUpdate();
                                    if(rSet==1){
                                        Intent intent = new Intent(change_phonenum.this, Mine.class);
                                        startActivity(intent);
                                        Log.e("TestDBConn", "更新手机号数据成功");
                                        finish();
                                    }
                                    else{
                                        Log.e("TestDBConn", "更新手机号数据失败");
                                    }
                                    preparedStatement.close();
                                    // Log.i("TestDBConn",rSet.getString("ac_name"));
                                } catch (SQLException e) {
                                    Log.e("TestDBConn", "createStatement error");
                                }

                            }

                        }
                    });
                    thread.start();
                    try {
                        conn.close();
                        Log.e("TestDBConn", "关闭连接成功！");
                    } catch (SQLException e) {
                        Log.e("TestDBConn", "关闭连接失败！");
                    }
                }
            }
        });

    }
}
