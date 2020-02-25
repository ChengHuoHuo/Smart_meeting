package com.example.yihuier_phone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Meeting_room_information_show extends AppCompatActivity {
    private TextView tv_room_name;
    private TextView tv_room_maxpeople;
    private TextView tv_room_status;
    private TextView tv_room_binding;
    private TextView tv_room_company;
    private Button btn_meeting_room_apply;
    private Button btn_meeting_room_picture;
    String u_account;
    private Connection conn=null;
    private int str;

    private String str_room_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_room_information_show);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);

        final MyApplication app=(MyApplication)getApplication();
        u_account=app.getAccount();

        Bundle bundle=getIntent().getExtras();
        str_room_id=bundle.getString("room_id");
        Toast.makeText(this,"会议室编号="+str_room_id,Toast.LENGTH_LONG).show();

        Set_room_information(str_room_id);
    }

    public void Set_room_information(final String s){
        tv_room_name=(TextView)findViewById(R.id.meeting_room_name);
        tv_room_maxpeople=(TextView)findViewById(R.id.meeting_room_maxpeople);

        tv_room_status=(TextView)findViewById(R.id.meeting_room_status);
        tv_room_binding=(TextView)findViewById(R.id.meeting_room_binding);
        tv_room_company=(TextView)findViewById(R.id.meeting_room_company);

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
                    String sql="select * from bz_company as bc,bz_room as br where room_id=? and bc.company_id=(select room_belong_company from bz_room where room_id=?);";
                    try {
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setString(1,s);
                        preparedStatement.setString(2,s);
                        ResultSet rSet=preparedStatement.executeQuery();
                        while (rSet.next()) {
                            if(rSet.getInt("room_status")==1){
                                tv_room_name.setText(rSet.getString("room_name"));
                                tv_room_maxpeople.setText(rSet.getString("room_capacity"));
                                if(rSet.getInt("room_parentstatus")==1)
                                {
                                    tv_room_status.setText("正常使用");
                                }
                                else {
                                    tv_room_status.setText("待维修");
                                }

                                if(rSet.getInt("room_if_occupy")==1){
                                    tv_room_binding.setText("已绑定");
                                    str=1;
                                }
                                else{
                                    tv_room_binding.setText("未绑定");
                                    str=0;
                                }
                                tv_room_company.setText(rSet.getString("company_name"));
                            }
                           else{
                                Toast.makeText(Meeting_room_information_show.this,"该会议室不存在",Toast.LENGTH_LONG).show();
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
