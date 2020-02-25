package com.example.yihuier_phone;

import android.content.Intent;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

public class Meeting_information_show extends AppCompatActivity {
    private final static int CONNECT_OUT_TIME = 5000;
    String u_account;
    private Connection conn=null;
    private TextView tv_meeting_name;
    private TextView tv_meeting_time;
    private TextView tv_meeting_area;
    private TextView tv_meeting_last_time;
    private TextView tv_meeting_content;
    private TextView tv_meeting_status;
    private Button btn_meeting_data;
    private Button btn_attend_meeting_people;
    private Button btn_meeting_notes;
    private Button btn_meeting_vote;
    private Button btn_meeting_record;
    private int str_checked_is_manager;
    private int str_checked_status;
    private String str1;
    private String str2;
    private int user_id;
    private LinearLayout linear_notes;
    private int check_user_collect_status;
    private String check_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_information_show);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);

        final MyApplication app=(MyApplication)getApplication();
        u_account=app.getAccount();
        user_id=app.getUserid();
        //找到控件
        final FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.bringToFront();
        Bundle bundle=getIntent().getExtras();
        final String str=bundle.getString("meeting_id");
        str1=str;
        Toast.makeText(this,str,Toast.LENGTH_LONG).show();
        getmeetingdata(str);
        Log.e("TestDBConn","sss+"+str1+" skkkkk+"+user_id);
        getusercollectiom(str1,user_id);
        Log.e("TestDBConn","收藏会议的status="+check_user_collect_status);
        if(check_user_collect_status==0||check_user_collect_status==2){
            fab.setImageResource(R.drawable.uncollect);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fab.setImageResource(R.drawable.shoucang);
                    go_collect(str1,user_id);
                    Toast.makeText(Meeting_information_show.this,"message="+check_message,Toast.LENGTH_LONG).show();
                }
            });
        }
        else{
            fab.setImageResource(R.drawable.shoucang);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    go_uncollect(str1,user_id);
                    fab.setImageResource(R.drawable.uncollect);
                    Toast.makeText(Meeting_information_show.this,"message="+check_message,Toast.LENGTH_LONG).show();
                }
            });
        }


        if(str_checked_status==2){//进行中的会议
            linear_notes=(LinearLayout)findViewById(R.id.linear_notes);
            linear_notes.setVisibility(View.VISIBLE);
            btn_meeting_notes=(Button)findViewById(R.id.btn_meeting_notes);
            btn_meeting_notes.setText("写会议笔记");
            btn_meeting_notes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //写会议笔记
                    gotowritenotes();
                }
            });
            if(str_checked_is_manager==1){//如果是会议发起人
                btn_meeting_vote=(Button)findViewById(R.id.btn_meeting_vote);
                btn_meeting_vote.setVisibility(View.VISIBLE);
                btn_meeting_vote.setText("发起投票");
                btn_meeting_vote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotovote();
                    }
                });
                btn_meeting_record=(Button)findViewById(R.id.btn_meeting_attend_record);
                btn_meeting_record.setVisibility(View.VISIBLE);
                btn_meeting_record.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotorecord();
                    }
                });
            }
            else{
                btn_meeting_vote=(Button)findViewById(R.id.btn_meeting_vote);
                btn_meeting_vote.setVisibility(View.VISIBLE);
                btn_meeting_vote.setText("参与投票");
                btn_meeting_vote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goto_attend_vote();
                    }
                });

                btn_meeting_record=(Button)findViewById(R.id.btn_meeting_attend_record);
                btn_meeting_record.setVisibility(View.GONE);
            }


        }
        else if(str_checked_status==1||str_checked_status==0){
            linear_notes=(LinearLayout)findViewById(R.id.linear_notes);
            linear_notes.setVisibility(View.GONE);

            if(str_checked_is_manager==1){
                btn_meeting_vote=(Button)findViewById(R.id.btn_meeting_vote);
                btn_meeting_vote.setVisibility(View.VISIBLE);
                btn_meeting_vote.setText("修改会议信息");
                btn_meeting_vote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotochange();
                    }
                });

                btn_meeting_record=(Button)findViewById(R.id.btn_meeting_attend_record);
                btn_meeting_record.setVisibility(View.VISIBLE);
                btn_meeting_record.setText("取消会议");
                btn_meeting_record.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotonagetive();
                    }
                });
            }
        }
        else if(str_checked_status==4){
            linear_notes=(LinearLayout)findViewById(R.id.linear_notes);
            linear_notes.setVisibility(View.GONE);
        }
        else {
            linear_notes=(LinearLayout)findViewById(R.id.linear_notes);
            linear_notes.setVisibility(View.VISIBLE);
            btn_meeting_notes=(Button)findViewById(R.id.btn_meeting_notes);
            btn_meeting_notes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoseenotes();
                }
            });
        }


        btn_meeting_data=(Button)findViewById(R.id.btn_meeting_data);
        btn_meeting_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotodataactivity();
            }
        });

        btn_attend_meeting_people=(Button)findViewById(R.id.btn_meeting_attend_person);
        btn_attend_meeting_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotosee_attend_poeple();
            }
        });

    }
    public void go_collect(final String s,final int userid){
        final String url="http://39.108.10.155:8080/UserManager/Users/collectMeeting.do";
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
                    //添加post请求的两行属性
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //传递参数
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", String.valueOf(userid));
                    params.put("meeting_id", s);
                    byte[] data = getRequestData(params,"utf-8").toString().getBytes();//获得请求体
                    //设置请求体的长度
                    connection.setRequestProperty("Content-Length", String.valueOf(data.length));
                    //获得输出流，向服务器写入数据
                    OutputStream out = connection.getOutputStream();
                    out.write(data);
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
                    Log.e("TestDBConn",buffer.toString());
                    reader.close();
                    JSONObject obj = new JSONObject( buffer.toString());
                    String message=obj.getString("message");
                    check_message=message;
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
    public void go_uncollect(final String s,final int userid){
        final String url="http://39.108.10.155:8080/UserManager/Users/outOfCollectMeeting.do";
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
                    //添加post请求的两行属性
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //传递参数
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", String.valueOf(userid));
                    params.put("meeting_id", s);
                    byte[] data = getRequestData(params,"utf-8").toString().getBytes();//获得请求体
                    //设置请求体的长度
                    connection.setRequestProperty("Content-Length", String.valueOf(data.length));
                    // 第四步：向服务器写入数据
                    OutputStream out = connection.getOutputStream();
                    out.write(data);
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
                    JSONObject obj = new JSONObject( buffer.toString());
                    check_message=obj.getString("message");
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (MalformedURLException e) {
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

    public void getusercollectiom(final String s,final int userid){
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
                    String sql="select collection_status from bz_collection where user_id=? and meeting_id=?";
                    try {
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setInt(1,userid);
                        preparedStatement.setString(2,s);
                        ResultSet rSet=preparedStatement.executeQuery();
                        if(rSet!=null)
                        {
                            while (rSet.next()) {
                                if(rSet.getString("collection_status").equals("1"))
                                {
                                    check_user_collect_status=1;//收藏了
                                }
                                else if(rSet.getString("collection_status").equals("0")){
                                    check_user_collect_status=2;//取消收藏
                                }
                                break;
                            }
                        }
                        else {
                            check_user_collect_status=0;//没有收藏
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
    /*
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }
    public void getmeetingdata(final String s){
        tv_meeting_name=(TextView)findViewById(R.id.meeting_name);
        tv_meeting_time=(TextView)findViewById(R.id.meeting_time);
        tv_meeting_area=(TextView)findViewById(R.id.meeting_area);
        tv_meeting_content=(TextView)findViewById(R.id.meeting_content);
        tv_meeting_status=(TextView)findViewById(R.id.meeting_status);
        tv_meeting_last_time=(TextView)findViewById(R.id.meeting_last_time);
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
                    String sql="select substring(bmr.meeting_time_start,1,19),bmr.meeting_time,bmu.user_id,bm.meeting_id,bmu.role,bm.meeting_status,bm.meeting_name,bm.description,br.room_name from bz_meeting as bm,bz_meeting_room as bmr,bz_room as br,bz_meeting_user as bmu where bm.meeting_id=bmr.meeting_id and bm.meeting_id=bmu.meeting_id and br.room_id=(select location from bz_meeting where meeting_id=?) and bm.meeting_id=? and bmu.user_id=(select user_id from bz_user where user_account=?)";
                    try {
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setString(1,s);
                        preparedStatement.setString(2,s);
                        preparedStatement.setString(3,u_account);
                        ResultSet rSet=preparedStatement.executeQuery();
                        while (rSet.next()) {
                            if(rSet.getString("bm.meeting_name")!=null){
                                str2=rSet.getString("bmu.user_id");
                                tv_meeting_name.setText(rSet.getString("bm.meeting_name"));
                                tv_meeting_time.setText(rSet.getString("substring(bmr.meeting_time_start,1,19)"));
                                tv_meeting_last_time.setText(rSet.getString("bmr.meeting_time"));
                                tv_meeting_area.setText(rSet.getString("br.room_name"));
                                tv_meeting_content.setText(rSet.getString("bm.description"));
                                if(rSet.getInt("bm.meeting_status")==4){
                                    tv_meeting_status.setText("已取消");
                                    str_checked_status=4;
                                }
                                else if(rSet.getInt("bm.meeting_status")==1||rSet.getInt("bm.meeting_status")==0){
                                    tv_meeting_status.setText("待开始");
                                    str_checked_status=1;
                                }
                                else if(rSet.getInt("bm.meeting_status")==2){
                                    tv_meeting_status.setText("进行中");
                                    str_checked_status=2;
                                }
                                else{
                                    tv_meeting_status.setText("已结束");
                                    str_checked_status=3;
                                }

                                if(rSet.getInt("bmu.role")==1){
                                    str_checked_is_manager=1;
                                }
                                else {
                                    str_checked_is_manager=0;
                                }

                                Log.e("TestDBConn", str_checked_status+"++sssss++"+str_checked_is_manager);
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

    public void gotowritenotes(){
        Bundle bundle=new Bundle();
        bundle.putString("meeting_id",str1);
        bundle.putString("user_id",str2);
        Intent intent=new Intent(Meeting_information_show.this,Write_Notes.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void gotodataactivity(){
        Bundle bundle=new Bundle();
        bundle.putString("meeting_id",str1);
        bundle.putString("user_id",str2);
        Intent intent=new Intent(Meeting_information_show.this,Meeting_Data.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void gotovote(){
        Bundle bundle=new Bundle();
        bundle.putString("meeting_id",str1);
        Intent intent=new Intent(Meeting_information_show.this,Meeting_Vote.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void gotorecord(){

    }

    public void gotochange(){

    }

    public void gotonagetive(){

    }
    public void goto_attend_vote(){

    }

    public void gotoseenotes(){
        Bundle bundle=new Bundle();
        bundle.putString("meeting_id",str1);
        Intent intent=new Intent(Meeting_information_show.this,Notes_show.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
    public void gotosee_attend_poeple(){
        Bundle bundle=new Bundle();
        bundle.putString("meeting_id",str1);
        Intent intent=new Intent(Meeting_information_show.this,Attend_meeting_information.class);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }
}
