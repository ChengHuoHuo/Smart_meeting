package com.example.yihuier_phone;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;



import android.os.Environment;

import com.example.yihuier_phone.Adapter.Attend_listview_adapter;
import com.example.yihuier_phone.Adapter.Data_listview;

import java.io.File;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class Meeting_Data extends AppCompatActivity {
    private final static int CONNECT_OUT_TIME = 5000;
    private String str_meeting_id;
    private String url;
    private ListView lv_meeting_data;
    private List<MeetingData> meeting_data_list;
    private MeetingData meetingData;
    private Data_listview adapter;
    private String str_message;
    private TextView tv_meeting_data_id;
    private String s_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_data);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);

        Bundle bundle=getIntent().getExtras();
        str_meeting_id=bundle.getString("meeting_id");
        lv_meeting_data=(ListView)findViewById(R.id.list_meeting_data) ;
        meeting_data_list=new ArrayList<>();
        get_meeting_data(str_meeting_id);
        initData();

        lv_meeting_data.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                s_id=meeting_data_list.get(position).getData_id().toString();
                Log.e("TestDBCONN","你点击的data_id="+s_id+"    position="+position);
                get_download_url(s_id);
                downloadFile(str_message);
            }
        });

        /**
         * 动态获取权限，Android 6.0 新特性，一些保护权限，除了要在AndroidManifest中声明权限，还要使用如下代码动态获取
         */
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }
    public void initData() {
        adapter = new Data_listview(Meeting_Data.this,meeting_data_list);
        lv_meeting_data.setAdapter(adapter);
    }
    public void get_meeting_data(final String smi){
        url="http://39.108.10.155:8080/MeetingManager/Meetings/MeetingFiles.json";
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
                    String content = "meeting_id=" + URLEncoder.encode(smi);// 无论服务器转码与否，这里不需要转码，因为Android系统自动已经转码为utf-8啦
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
                    Log.e("TestDBCONN",buffer.toString());
                    JSONObject obj = new JSONObject( buffer.toString());
                    JSONArray user = null;//通过user字段获取其所包含的JSONObject对象
                    user = obj.getJSONArray("data");
                    for(int i=0;i<user.length();i++){
                        meetingData=new MeetingData();
                        JSONObject user_json=user.getJSONObject(i);
                        String file_name = user_json.getString("file_name");//通过name字段获取其所包含的字符串
                        Log.e("TestDBConn",file_name);
                        String data_id=user_json.getString("data_id");
                        meetingData.setData_id(data_id);
                        meetingData.setData_name(file_name);
                        meeting_data_list.add(meetingData);
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
    public void get_download_url(final String s_id){
        url="http://39.108.10.155:8080/Common/download/getFileForAndroid.file";
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
                    String content = "data_id=" + URLEncoder.encode(s_id);// 无论服务器转码与否，这里不需要转码，因为Android系统自动已经转码为utf-8啦
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
                    String file = null;//通过user字段获取其所包含的JSONObject对象
                    file = obj.getString("data");
                    str_message=file;
                    Log.e("TestDBCONN","data_id="+s_id+"  url="+buffer.toString());
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
    public void downloadFile(final String url1){
        //下载路径，如果路径无效了，可换成你的下载路径
        final String url = url1;
        Toast.makeText(Meeting_Data.this,url,Toast.LENGTH_LONG).show();
        final long startTime = System.currentTimeMillis();
        Log.e("DOWNLOAD","startTime="+startTime);
        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 下载失败             e.printStackTrace();
                Toast.makeText(Meeting_Data.this,"下载失败",Toast.LENGTH_LONG).show();
                Log.e("DOWNLOAD","download failed");
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    String mSDCardPath=Environment.getExternalStorageDirectory()+"/yihuier/";
                    File file_path=new File(mSDCardPath);
                    if(!file_path.exists()){
                        file_path.mkdir();
                    }
                    File dest = new File(mSDCardPath,url.substring(url.lastIndexOf("/") + 1));
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();
                    Log.e("DOWNLOAD","download success");
                    Looper.prepare();
                    Toast.makeText(Meeting_Data.this,"下载成功",Toast.LENGTH_LONG).show();
                    Looper.loop();
                    Log.e("DOWNLOAD","totalTime="+ (System.currentTimeMillis() - startTime));
                } catch (Exception e) {
                    e.printStackTrace();
                    Looper.prepare();
                    Toast.makeText(Meeting_Data.this,"下载失败",Toast.LENGTH_LONG).show();
                    Looper.loop();
                    Log.e("DOWNLOAD","download failed");
                } finally {
                    if(bufferedSink != null){
                        bufferedSink.close();
                    }

                }
            }
        });
    }
}
