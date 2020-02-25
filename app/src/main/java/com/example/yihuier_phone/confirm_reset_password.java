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

public class confirm_reset_password extends AppCompatActivity {
    EditText et_password1;
    EditText et_password2;
    private Connection conn=null;
    String u_account;
    private MD5Utils md5Utils = new MD5Utils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_reset_password);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);

        final MyApplication app=(MyApplication)getApplication();
        u_account=app.getAccount();

        et_password1=(EditText)findViewById(R.id.et_new_password);
        et_password2=(EditText)findViewById(R.id.et_confirm_new_password);

        Button btn_changepassword=(Button)findViewById(R.id.confirm);
        btn_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String p1=et_password1.getText().toString();
                final String p2=et_password2.getText().toString();
                final String pwd=md5Utils.toMd5(p1);

                if(p1.equals(p2)&&p1!=null&&p2!=null){
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
                                String sql ="update bz_user set user_password=? where user_account=? ";
                                try {
                                    // 创建用来执行sql语句的对象
                                    PreparedStatement preparedStatement = conn.prepareStatement(sql);
                                    preparedStatement.setString(1, pwd);
                                    Log.e("TestDBConn", "kkkkk+"+pwd);
                                    preparedStatement.setString(2,u_account);
                                    Log.e("TestDBConn", "ssss+"+u_account);
                                    // 执行sql查询语句并获取查询信息
                                    int rSet = preparedStatement.executeUpdate();
                                    if(rSet==1){
                                        Intent intent = new Intent(confirm_reset_password.this, LoginActivity.class);
                                        startActivity(intent);
                                        Log.e("TestDBConn", "更新密码数据成功");
                                        finish();
                                    }
                                    else{
                                        Log.e("TestDBConn", "更新密码数据失败");
                                    }
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
                else if(!p1.equals(p2))
                    {
                    Toast.makeText(confirm_reset_password.this,"两次密码不一致，请重新输入",Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(confirm_reset_password.this,"请输入有效密码",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
