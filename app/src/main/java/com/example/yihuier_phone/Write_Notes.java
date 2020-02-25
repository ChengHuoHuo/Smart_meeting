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
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class Write_Notes extends AppCompatActivity {
    private Connection conn=null;
    private String notes;
    private EditText et_notes;
    String str1;
    String str2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_notes);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);

        Bundle bundle=getIntent().getExtras();
        str1=bundle.getString("meeting_id");
        str2=bundle.getString("user_id");
        Log.e("TestDBConn", "ssss+"+str1+"kkkkk+"+str2);

        Button btn_commit_notes=(Button)findViewById(R.id.btn_commit_notes);
        btn_commit_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_notes=(EditText)findViewById(R.id.et_write_notes);
                notes=et_notes.getText().toString();
                if(notes!=null){
                    gotocommitnotes(str1,str2,notes);
                }
                else {
                    Toast.makeText(Write_Notes.this,"笔记不能为空",Toast.LENGTH_LONG).show();
                }
            }
        });


    }
    public void gotocommitnotes(final String s1,final String s2,final String notes){
        final java.util.Date date=new java.util.Date();
        final Date date1=new Date(date.getTime());
        final Timestamp date2=new Timestamp(date.getTime());

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
                    String sql ="insert into bz_note (meeting_id,user_id,note_content,note_publish_date,note_publish_datetime,note_privilege,note_view) values(?,?,?,?,?,1,0)";
                    try {
                        // 创建用来执行sql语句的对象
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setString(1, s1);
                        preparedStatement.setString(2,s2);
                        preparedStatement.setString(3,notes);
                        preparedStatement.setDate(4,date1 );
                        preparedStatement.setTimestamp(5,date2);
                        // 执行sql查询语句并获取查询信息
                        int rSet = preparedStatement.executeUpdate();
                        if(rSet==1){
                            Intent intent1 = new Intent(Write_Notes.this, MainActivity.class);
                            startActivity(intent1);
                            Log.e("TestDBConn", "插入数据成功");
                            finish();
                        }
                        else{
                            Log.e("TestDBConn", "插入数据失败");
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
}
