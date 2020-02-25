package com.example.yihuier_phone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yihuier_phone.Adapter.Attend_listview_adapter;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Attend_meeting_information extends AppCompatActivity {
    private ListView lv_attend_people;
    private final static int CONNECT_OUT_TIME = 5000;
    private String url;
    private Attend_listview_adapter adapter;
    private String str;
    private People_attend people_attend;
    private List<People_attend> list_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attend_meeting_information);
        lv_attend_people=(ListView)findViewById(R.id.list_attend_people);

        Bundle bundle=getIntent().getExtras();
        str=bundle.getString("meeting_id");
        list_url=new ArrayList<>();
        getpeopledata(str);
        initDate();
        lv_attend_people.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("TestDBCONN","你点击的position="+position);
            }
        });
    }
    private void initDate() {
        adapter = new Attend_listview_adapter(Attend_meeting_information.this,list_url);
        lv_attend_people.setAdapter(adapter);
    }

    public void getpeopledata(String s){
        url="http://39.108.10.155:8080/MeetingManager/Meetings/meetingUsers.json";
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
                    String content = "meeting_id=" + URLEncoder.encode(str);// 无论服务器转码与否，这里不需要转码，因为Android系统自动已经转码为utf-8啦
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
                    JSONArray user = null;//通过user字段获取其所包含的JSONObject对象
                    user = obj.getJSONArray("data");
                    for(int i=0;i<user.length();i++){
                        people_attend=new People_attend();
                        JSONObject user_json=user.getJSONObject(i);
                        String image_name = user_json.getString("user_headpic_dir");//通过name字段获取其所包含的字符串
                        Log.e("TestDBCONN",image_name);
                        String name=user_json.getString("user_name");
                        people_attend.setTextTitle(name);
                        people_attend.setUrl(image_name);
                        list_url.add(people_attend);
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
