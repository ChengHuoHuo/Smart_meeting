package com.example.yihuier_phone.Page;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.yihuier_phone.ActivityCollectorUtil;
import com.example.yihuier_phone.Mine;
import com.example.yihuier_phone.MyApplication;
import com.example.yihuier_phone.My_attend_meeting;
import com.example.yihuier_phone.R;
import com.example.yihuier_phone.User.UserManage;

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

import de.hdodenhof.circleimageview.CircleImageView;

public class Pagemine extends Fragment {
    private final static int CONNECT_OUT_TIME = 5000;
    private String url;
    private int user_id;
    private String str_user_image_url;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.minelayout, container, false);

        TextView tv_name=(TextView)view.findViewById(R.id.mine_user_name);
        TextView tv_account=(TextView)view.findViewById(R.id.mine_user_account);
        MyApplication app=(MyApplication)getActivity().getApplication();
        if (app.getName()!=null&&app.getAccount()!=null){
            String username=app.getName();
            String useraccount=app.getAccount();
            tv_name.setText("姓名："+username);
            tv_account.setText("工号："+useraccount);
            user_id=app.getUserid();
        }
        get_image_url();

        Toast.makeText(getContext(),"照片的路径为"+str_user_image_url,Toast.LENGTH_LONG).show();
        CircleImageView changeMyInformation=(CircleImageView)view.findViewById(R.id.mine_user_image);
        Glide.with(getContext()).load(str_user_image_url).into(changeMyInformation);
        changeMyInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Mine.class);
                Bundle bundle=new Bundle();
                bundle.putString("picture_url",str_user_image_url);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        LinearLayout linear_my_meeting=(LinearLayout)view.findViewById(R.id.linear_mymeeting);
        linear_my_meeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),My_attend_meeting.class);
                Bundle bundle=new Bundle();
                bundle.putInt("all",1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        LinearLayout linear_my_coolection=(LinearLayout)view.findViewById(R.id.user_meeting_collect);
        linear_my_coolection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),My_attend_meeting.class);
                Bundle bundle=new Bundle();
                bundle.putString("user_id", String.valueOf(user_id));
                bundle.putInt("all",0);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ImageButton btn_mymeeting=(ImageButton) view.findViewById(R.id.btn_mymeeting);
        btn_mymeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),My_attend_meeting.class);
                Bundle bundle=new Bundle();
                bundle.putInt("all",1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        ImageButton btn_my_nocomplete=(ImageButton) view.findViewById(R.id.meeting_nocomplete);
        btn_my_nocomplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),My_attend_meeting.class);
                Bundle bundle=new Bundle();
                bundle.putInt("all",2);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ImageButton btn_meeting_ing=(ImageButton) view.findViewById(R.id.meeting_ing);
        btn_meeting_ing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),My_attend_meeting.class);
                Bundle bundle=new Bundle();
                bundle.putInt("all",3);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ImageButton btn_meeting_complete=(ImageButton) view.findViewById(R.id.meeting_complete);
        btn_meeting_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),My_attend_meeting.class);
                Bundle bundle=new Bundle();
                bundle.putInt("all",4);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        ImageButton btn_meeting_nagetive=(ImageButton) view.findViewById(R.id.meeting_nagetive);
        btn_meeting_nagetive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),My_attend_meeting.class);
                Bundle bundle=new Bundle();
                bundle.putInt("all",5);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        LinearLayout linear_exit=(LinearLayout)view.findViewById(R.id.exit);
        linear_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               exitDialog();
            }
        });

        return view;
    }
    public void exitDialog(){
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(getContext());
        normalDialog.setIcon(R.drawable.logo);
        normalDialog.setTitle("退出");
        normalDialog.setMessage("您确定要退出吗TAT?");
        normalDialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UserManage.getInstance().clearUserInfo(getContext());
                ActivityCollectorUtil.finishAllActivity();
            }
        });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }
    public void get_image_url(){
        url="http://39.108.10.155:8080/UserManager/Users/userHeaderPic.json";
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
                    String content = "user_id=" + URLEncoder.encode(String.valueOf(user_id));// 无论服务器转码与否，这里不需要转码，因为Android系统自动已经转码为utf-8啦
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
                    Log.e("TestDBConn",buffer.toString());
                    JSONObject obj = new JSONObject( buffer.toString());
                    JSONObject user = null;//通过user字段获取其所包含的JSONObject对象
                    user = obj.getJSONObject("data");
                    String name = user.getString("user_headpic_dir");//通过name字段获取其所包含的字符串
                    str_user_image_url=name;
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
