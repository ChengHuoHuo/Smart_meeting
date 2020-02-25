package com.example.yihuier_phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.animation.Positioning;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.yihuier_phone.Adapter.PeopleAdapter;
import com.example.yihuier_phone.Adapter.U;
import com.example.yihuier_phone.Entity.People;
import com.example.yihuier_phone.personalinfo.PersonalMsgActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
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

import static android.content.ContentValues.TAG;

public class MeetingPeopleActivity extends Activity {

    private TextView myTextView;
    private List<String> data=new ArrayList<>();
    private ArrayAdapter arrayadapter;
    String cardNumber;
    private  Spinner mySpinner1;
    String department_name;
    private final static int CONNECT_OUT_TIME = 5000;
    String url;
    private Handler uiHandler = new Handler(){
        // 覆写这个方法，接收并处理消息。
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    arrayadapter.notifyDataSetChanged();
                    mySpinner1.setAdapter(arrayadapter);
                    break;
                case 2:
                    peopleadapter.notifyDataSetChanged();
                    lv.setAdapter(peopleadapter);
                    break;
            }}};


    private List<People> fruitList=new ArrayList<>();
    PeopleAdapter peopleadapter;
    String sqlarray;
    String sqlpeople;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.available_people);
        // 第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项
        connectionarray();

/*
        ImageView imageView=findViewById(R.id.xuanhaole);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });*/
        myTextView = (TextView) findViewById(R.id.textView);
        mySpinner1= (Spinner) findViewById(R.id.spinner);


        peopleadapter=new PeopleAdapter(this, R.layout.peoplemsg, fruitList);

        lv=(ListView)findViewById(R.id.availablelv);
      lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //点击后在标题上显示点击了第几行

            }
        });

        // 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
        arrayadapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,data);

        // 第三步：为适配器设置下拉列表下拉时的菜单样式。
        arrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 第四步：将适配器添加到下拉列表上


        // 第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中

        mySpinner1
                .setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        cardNumber = data.get(arg2);
                        //设置显示当前选择的项
                        Log.d("nnnnn",cardNumber);
                        arg0.setVisibility(View.VISIBLE);
                        peopleadapter.notifyDataSetChanged();
                        peopleadapter.clear();
                        connectionpeople();


                    }

                    public void onNothingSelected(AdapterView<?> arg0) {
                        /*myTextView.setText("部门");*/
                    }
                });


    }


    public void connectionarray() {

        url="http://39.108.10.155:8080/UserManager/Users/getPersonalCompanyUsers.json";
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
                    String content = "user_id=" + URLEncoder.encode(String.valueOf(1));// 无论服务器转码与否，这里不需要转码，因为Android系统自动已经转码为utf-8啦
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
                    Log.e("aaaa",buffer.toString());
                    JSONObject obj1 = new JSONObject( buffer.toString());
                    JSONArray user = null;//通过user字段获取其所包含的JSONObject对象
                    user = obj1.getJSONArray("data");

                    for(int i=0;i<user.length();i++){

                        JSONObject user_json=user.getJSONObject(i);
                        String department_name = user_json.getString("department_name");//通过name字段获取其所包含的字符串
                        Log.e("aaa","message="+department_name);

                           data.add(department_name);

                    }
                    Message msg = new Message();
                    msg.what = 1;
                    Log.e("aaa","鬼泣五天下第一！");
                    uiHandler.sendMessage(msg);
                   /* JSONObject obj = new JSONObject( buffer.toString());
                    JSONArray detuser=obj.getJSONArray("data");
*/

                    /*for(int i=0;i<detuser.length();i++) {
                        JSONObject obj1aa = detuser.getJSONObject(i);
                        department_name=obj1aa.getString("department_name");
                        JSONArray jsonArray = obj1aa.getJSONArray("users");
                        JSONObject users = null;
                        data.add(department_name);
                        users = jsonArray.getJSONObject(0);*/
                       /* for (int j = 0; j < jsonArray.length(); j++) {
                            users = jsonArray.getJSONObject(0);

                            people_order people_order = new people_order();
                            String name = users.getString("user_name");
                            //通过name字段获取其所包含的字符串

                            int id = Integer.parseInt(users.getString("user_id"));
                            people_order.setUser_id(id);
                            Log.e("aaa", "好嗨哟" + name);
                            people_order.setName(name);
                            U u=new U();
                            u.setName(name);


                            people_order.setDepartment_name(department_name);

                        }*/
                       /* for( i=0;i<jsonArray.length(); i++){

                            Log.e("adsa",userlist.get(i)+"的解放军饿哦王健林的看法");
                        }*/









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
    public void connectionpeople() {



        final Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {



                // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                String ip = "47.96.104.134";
                int port = 3306;
                String dbName = "bz_meeting";
                String url = "jdbc:mysql://" + ip + ":" + port + "/" + dbName; // 构建连接mysql的字符串
                String user = "root";
                String password = "Sayello.123";

                // 3.连接JDBC
                // 3.连接JDBC
                Connection conn = null;
                try {
                    conn = DriverManager.getConnection(url, user, password);

                } catch (SQLException e) {
                    Log.e(TAG, "远程连接失败!");
                }

                if (conn != null) {
                    int s=90;

                        sqlpeople = "select user_name from bz_user where user_id in (select user_id from bz_department_user where department_id in(select department_id from bz_department  where department_name =?)) ";

                    try {

                        PreparedStatement preparedStatement = conn.prepareStatement(sqlpeople);
                        preparedStatement.setString(1,cardNumber);
                        ResultSet rSet = preparedStatement.executeQuery();


                        while (rSet.next()){
                            People people=new People();
                            people.setName(rSet.getString("user_name"));
                            Log.d("kkk",people.getName());
                            fruitList.add(people);

                        }
                        Message msg = new Message();
                        msg.what = 2;
                        uiHandler.sendMessage(msg);
                        Log.e("aaa","鬼泣五天下第二！");

                        rSet.close();
                        preparedStatement.close();
                    } catch (SQLException e) {
                        Log.e(TAG, "createStatement error");
                    }

                    try {
                        conn.close();
                    } catch (SQLException e) {
                        Log.e(TAG, "关闭连接失败");
                    }
                }

            }
        });
        thread2.start();

    }
}