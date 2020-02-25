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
import java.sql.SQLException;

public class changemail extends AppCompatActivity {

    EditText et_email;
    private Connection conn=null;
    String u_account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changemail);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);

        final MyApplication app=(MyApplication)getApplication();
        u_account=app.getAccount();
        et_email=(EditText)findViewById(R.id.et_new_email);

        Button btn_changephonenum=(Button)findViewById(R.id.confirm);
        btn_changephonenum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String e=et_email.getText().toString();
                if(!e.matches("[\\w\\.\\-]+@([\\w\\-]+\\.)+[\\w\\-]+")){
                    Toast.makeText(changemail.this,"邮箱格式错误，请重新输入",Toast.LENGTH_LONG).show();
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
                                String sql ="update bz_user set user_email=? where user_account=? ";
                                try {
                                    // 创建用来执行sql语句的对象
                                    PreparedStatement preparedStatement = conn.prepareStatement(sql);
                                    preparedStatement.setString(1, e);
                                    Log.e("TestDBConn", "kkkkk+"+e);
                                    preparedStatement.setString(2,u_account);
                                    Log.e("TestDBConn", "ssss+"+u_account);
                                    // 执行sql查询语句并获取查询信息
                                    int rSet = preparedStatement.executeUpdate();
                                    if(rSet==1){
                                        Intent intent = new Intent(changemail.this, Mine.class);
                                        startActivity(intent);
                                        Log.e("TestDBConn", "更新邮箱数据成功");
                                        finish();
                                    }
                                    else{
                                        Log.e("TestDBConn", "更新邮箱数据失败");
                                    }
                                    preparedStatement.close();
                                    // Log.i("TestDBConn",rSet.getString("ac_name"));
                                } catch (SQLException e) {
                                    Log.e("TestDBConn", "createStatement error");
                                }
                                try {
                                    conn.close();
                                    Log.e("TestDBConn", "关闭连接成功！");
                                } catch (SQLException e) {
                                    Log.e("TestDBConn", "关闭连接失败！");
                                }
                            }
                        }
                    });
                    thread.start();
                }
            }
        });

    }
}
