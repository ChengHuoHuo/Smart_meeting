package com.example.yihuier_phone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class resetpassword extends AppCompatActivity {
    private Connection conn=null;
    String u_account;
    EditText et_answer1;
    EditText et_answer2;
    EditText et_answer3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);


        MyApplication app1=(MyApplication)getApplication();
        u_account=app1.getAccount();

//        Button btn_back=(Button)findViewById(R.id.btn_back);
//        btn_back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(resetpassword.this, Mine.class);
//                startActivity(intent);
//            }
//        });
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
                                preparedStatement.setString(1,u_account);
                                // 执行sql查询语句并获取查询信息
                                ResultSet rSet = preparedStatement.executeQuery();
                                while (rSet.next()){
                                    if(rSet.getString("user_account").equals(u_account)){
                                        Log.e("TestDBConn", rSet.getString("user_security_question1_answer")+"   "+rSet.getString("user_security_question2_answer")+"     "+rSet.getString("user_security_question3_answer")+" ");

                                        Log.e("TestDBConn",rSet.getInt("user_security_question1_no")+"  "+rSet.getInt("user_security_question2_no")+"  " +rSet.getInt("user_security_question3_no"));
                                        gotosetquestion(rSet.getInt("user_security_question1_no"),rSet.getInt("user_security_question2_no"),rSet.getInt("user_security_question3_no"));

                                        gotochangepassword(rSet.getString("user_security_question1_answer"),rSet.getString("user_security_question2_answer"),rSet.getString("user_security_question3_answer"));
                                        break;
                                    }
                                    else {
                                        Toast.makeText(resetpassword.this,"不存在该用户",Toast.LENGTH_LONG).show();
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
    public void gotosetquestion(final int s,final int k,final int p){
        final TextView tv_question1=(TextView)findViewById(R.id.first_question);
        final TextView tv_question2=(TextView)findViewById(R.id.second_question);
        final TextView tv_question3=(TextView)findViewById(R.id.third_question);
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
                    String sql1 = "SELECT question_content FROM bz_question where question_id=?" ;
                    try {
                        // 创建用来执行sql语句的对象
                        PreparedStatement preparedStatement1 = conn.prepareStatement(sql1);
                        preparedStatement1.setInt(1,s);
                        ResultSet rSet1 = preparedStatement1.executeQuery();
                        while (rSet1.next()){
                            if(rSet1.getString("question_content")!=null){
                                tv_question1.setText(rSet1.getString("question_content"));
                                break;
                            }
                        }
                        rSet1.close();
                        preparedStatement1.close();

                        PreparedStatement preparedStatement2 = conn.prepareStatement(sql1);
                        preparedStatement2.setInt(1,k);
                        ResultSet rSet2 = preparedStatement2.executeQuery();
                        while (rSet2.next()){
                            if(rSet2.getString("question_content")!=null){
                                tv_question2.setText(rSet2.getString("question_content"));
                                break;
                            }
                        }
                        rSet2.close();
                        preparedStatement2.close();

                        PreparedStatement preparedStatement3 = conn.prepareStatement(sql1);
                        preparedStatement3.setInt(1,p);
                        ResultSet rSet3 = preparedStatement3.executeQuery();
                        while (rSet3.next()){
                            if(rSet3.getString("question_content")!=null){
                                tv_question3.setText(rSet3.getString("question_content"));
                                break;
                            }
                        }
                        rSet3.close();
                        preparedStatement3.close();



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

    public void gotochangepassword(final String s, final String k, final String p){
        et_answer1=(EditText)findViewById(R.id.first_answer);
        et_answer2=(EditText)findViewById(R.id.second_answer);
        et_answer3=(EditText)findViewById(R.id.third_answer);
        Button btn_next=(Button)findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String one=et_answer1.getText().toString();
                final String two=et_answer2.getText().toString();
                final String third=et_answer3.getText().toString();
                if(s.equals(one)&&k.equals(two)&&p.equals(third)){
                    Intent intent = new Intent(resetpassword.this, confirm_reset_password.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(resetpassword.this,"输入不能为空",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
