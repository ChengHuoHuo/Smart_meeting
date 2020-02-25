package com.example.yihuier_phone;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class My_attend_meeting extends AppCompatActivity {
    private final static int CONNECT_OUT_TIME = 5000;
    private ListView lv_mine_attend_meeting;
    String u_account;
    private Connection conn=null;
    private  List<Map<String, Object>> data_mine_attend_meeting;
    SimpleAdapter adapter;
    private TextView tv_meeting_id;
    private String str;
    private int str_all;
    private String coleect_user_id;
    private String meeting_message;
    private TextView tv_meeting_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_attend_meeting);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);

        final MyApplication app=(MyApplication)getApplication();
        u_account=app.getAccount();
        Bundle bundle_all=getIntent().getExtras();
        str_all=bundle_all.getInt("all");
        coleect_user_id=bundle_all.getString("user_id");
        Log.e("TestDBConn","收藏会议列表的user_id="+coleect_user_id);
        lv_mine_attend_meeting=(ListView)findViewById(R.id.list_mine_attend_meeting);
        if(str_all>=1&&str_all<=5){
            getdata();
        }
        else if(str_all==0) {
            getcollection(coleect_user_id);
            tv_meeting_title=(TextView)findViewById(R.id.tv_my_meeting);
            tv_meeting_title.setText("我收藏的会议");
            Toast.makeText(My_attend_meeting.this,meeting_message,Toast.LENGTH_LONG).show();
        }
        adapter=new SimpleAdapter(this,data_mine_attend_meeting,R.layout.mine_attend_item,new String[] {"meeting_id","meeting_name","meeting_time","meeting_status"},new int[]{R.id.mine_meeting_id,R.id.mine_meeting_name,R.id.mine_meeting_time,R.id.mine_meeting_status});
        lv_mine_attend_meeting.setAdapter(adapter);


        lv_mine_attend_meeting.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_meeting_id=(TextView)view.findViewById(R.id.mine_meeting_id);
                str=tv_meeting_id.getText().toString();
                Toast.makeText(My_attend_meeting.this, "" + str, Toast.LENGTH_LONG).show();//显示数据

                Intent intent=new Intent(My_attend_meeting.this,Meeting_information_show.class);
                Bundle bundle=new Bundle();
                bundle.putString("meeting_id",str);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    public void getdata(){
        data_mine_attend_meeting = new ArrayList<Map<String, Object>>();
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
                    String sql="select  substring(bmr.meeting_time_start,1,19),bm.meeting_id,bm.meeting_name,bm.meeting_status from bz_meeting_room as bmr,bz_meeting as bm where bmr.meeting_id=bm.meeting_id and bm.meeting_id in (select meeting_id from bz_meeting_user where user_id=(select user_id from bz_user where user_account= ? ))";
                    try {
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setString(1,u_account);
                        ResultSet rSet=preparedStatement.executeQuery();
                        while (rSet.next()){
                            Log.e("TestDBConn", rSet.getString("bm.meeting_name") + "      " + rSet.getString("substring(bmr.meeting_time_start,1,19)") + "   " + rSet.getInt("bm.meeting_status"));

                            if(str_all==1) {
                                Map map = new HashMap();
                                map.put("meeting_id", rSet.getInt("bm.meeting_id"));
                                map.put("meeting_name", rSet.getString("bm.meeting_name"));
                                map.put("meeting_time", rSet.getString("substring(bmr.meeting_time_start,1,19)"));
                                if (rSet.getInt("bm.meeting_status") == 4) {
                                    map.put("meeting_status", "已取消");
                                } else if (rSet.getInt("bm.meeting_status") == 1 || rSet.getInt("bm.meeting_status") == 0) {
                                    map.put("meeting_status", "待开始");
                                } else if (rSet.getInt("bm.meeting_status") == 2) {
                                    map.put("meeting_status", "进行中");
                                } else if (rSet.getInt("bm.meeting_status") == 3) {
                                    map.put("meeting_status", "已结束");
                                }
                                data_mine_attend_meeting.add(map);
                            }
                            else if(str_all==2){
                                if(rSet.getInt("bm.meeting_status") == 1 || rSet.getInt("bm.meeting_status") == 0){
                                    Map map = new HashMap();
                                    map.put("meeting_id", rSet.getInt("bm.meeting_id"));
                                    map.put("meeting_name", rSet.getString("bm.meeting_name"));
                                    map.put("meeting_time", rSet.getString("substring(bmr.meeting_time_start,1,19)"));
                                    map.put("meeting_status", "待开始");
                                    data_mine_attend_meeting.add(map);
                                }
                            }
                            else if(str_all==3){
                                if(rSet.getInt("bm.meeting_status") ==2){
                                    Map map = new HashMap();
                                    map.put("meeting_id", rSet.getInt("bm.meeting_id"));
                                    map.put("meeting_name", rSet.getString("bm.meeting_name"));
                                    map.put("meeting_time", rSet.getString("substring(bmr.meeting_time_start,1,19)"));
                                    map.put("meeting_status", "进行中");
                                    data_mine_attend_meeting.add(map);
                                }
                            }
                            else if(str_all==4){
                                if(rSet.getInt("bm.meeting_status") ==3){
                                    Map map = new HashMap();
                                    map.put("meeting_id", rSet.getInt("bm.meeting_id"));
                                    map.put("meeting_name", rSet.getString("bm.meeting_name"));
                                    map.put("meeting_time", rSet.getString("substring(bmr.meeting_time_start,1,19)"));
                                    map.put("meeting_status", "已结束");
                                    data_mine_attend_meeting.add(map);
                                }
                            }
                            else {
                                if(rSet.getInt("bm.meeting_status") ==4){
                                    Map map = new HashMap();
                                    map.put("meeting_id", rSet.getInt("bm.meeting_id"));
                                    map.put("meeting_name", rSet.getString("bm.meeting_name"));
                                    map.put("meeting_time", rSet.getString("substring(bmr.meeting_time_start,1,19)"));
                                    map.put("meeting_status", "已取消");
                                    data_mine_attend_meeting.add(map);
                                }
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

    public void getcollection(final String user_id){
        data_mine_attend_meeting = new ArrayList<Map<String, Object>>();
        final String url="http://39.108.10.155:8080/MeetingManager/Meetings/collectionMeetings.json";
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
                    String content = "user_id=" + URLEncoder.encode(user_id);// 无论服务器转码与否，这里不需要转码，因为Android系统自动已经转码为utf-8啦
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
                    JSONObject obj = new JSONObject( buffer.toString());
                    Log.e("TestDBConn",buffer.toString());
                    JSONArray user = null;//通过user字段获取其所包含的JSONObject对象
                    meeting_message=obj.getString("message");
                    if(obj.getString("code").equals("0")){
                        user=obj.getJSONArray("data");
                        for(int i=0;i<user.length();i++){
                            Map map = new HashMap();
                            JSONObject user_json=user.getJSONObject(i);
                            String meeting_id = user_json.getString("meeting_id");//通过name字段获取其所包含的字符串
                            String meeting_name=user_json.getString("meeting_name");
                            String meeting_time=user_json.getString("startTime");
                            String meeting_status=user_json.getString("meeting_status");
                            map.put("meeting_id",meeting_id);
                            map.put("meeting_name",meeting_name);
                            map.put("meeting_time",meeting_time);
                            if(meeting_status.equals("1")||meeting_status.equals("0")){
                                map.put("meeting_status","待开始");
                            }
                            else if(meeting_status.equals("2")){
                                map.put("meeting_status","进行中");
                            }
                            else if(meeting_status.equals("3")){
                                map.put("meeting_status","已结束");
                            }
                            else {
                                map.put("meeting_status","已取消");
                            }
                            data_mine_attend_meeting.add(map);
                        }
                    }
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

}