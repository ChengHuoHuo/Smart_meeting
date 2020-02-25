package com.example.yihuier_phone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Note_show_information extends AppCompatActivity {

    private Connection conn=null;
    private TextView tv_note_name;
    private TextView tv_note_content;
    private String str;
    private String str1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_show_information);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);
        Bundle bundle=getIntent().getExtras();
        str=bundle.getString("note_id");
        str1=bundle.getString("meeting_id");
        get_note_data(str,str1);

    }
    public void get_note_data(final String s,final String k){
        tv_note_name=(TextView)findViewById(R.id.tv_note_name);
        tv_note_content=(TextView)findViewById(R.id.tv_note_content);
        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
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
                    String sql="SELECT bm.name,bn.note_content from bz_note as bn,bz_meeting as bm where bn.note_id=? and bm.meeting_id=?;";
                    try {
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setString(1,s);
                        preparedStatement.setString(2,k);
                        ResultSet rSet=preparedStatement.executeQuery();
                        while (rSet.next()){
                            if(rSet.getString("meeting_id")!=null)
                            {
                                tv_note_name.setText(rSet.getString("bm.name"));
                                tv_note_content.setText(rSet.getString("note_content"));
                                break;
                            }
                        }
                        rSet.close();
                        preparedStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            conn.close();
            Log.e("TestDBConn", "关闭连接成功！");
        } catch (SQLException e) {
            Log.e("TestDBConn", "关闭连接失败！");
        }
    }
}
