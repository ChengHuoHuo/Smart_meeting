package com.example.yihuier_phone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.yihuier_phone.Adapter.FruitAdapter;
import com.example.yihuier_phone.Adapter.MeetingAdapter;
import com.example.yihuier_phone.Adapter.U;
import com.example.yihuier_phone.Entity.Meeting;
import com.example.yihuier_phone.Page.MsgDetailActivity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by yifeng on 16/8/3.
 *
 */
public class tabcontentmsgfragment extends Fragment {
    private List<Meeting> meetingList=new ArrayList<>();
    private static final String EXTRA_CONTENT = "content";
    private Handler uiHandler = new Handler(){
        // 覆写这个方法，接收并处理消息。
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    adapter.notifyDataSetChanged();
                    listView.setAdapter(adapter);
                    break;
            }}};

    List<String> list;

 MeetingAdapter adapter;
    private ListView listView;



    public static TabContentFragment newInstance(String content){
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_CONTENT, content);
        TabContentFragment tabContentFragment = new TabContentFragment();
        tabContentFragment.setArguments(arguments);
        return tabContentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_tabcontent,container,false);

        /*   initFruits();*/
        list = new ArrayList<String>();
        connection(list);

        adapter=new MeetingAdapter(getActivity(),R.layout.msgdetail,meetingList);
        listView=(ListView)view.findViewById(R.id.msglv1);


        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Meeting user=meetingList.get(position);
                Intent intent = new Intent(getActivity(), MsgDetailActivity.class);
                Bundle bundle=new Bundle();
                bundle.putString("name",user.getName());
                bundle.putString("theme",user.getTheme());
                bundle.putString("time",user.getTime());
                bundle.putString("content",user.getContent());
                bundle.putString("des",user.getDes());
                bundle.putInt("num",user.getNum());
                intent.putExtras(bundle); // 为intent 设置bundle
                startActivity(intent);
            }
        });

        return view;
    }


    public boolean connection(final List<String> list) {



        final Thread thread = new Thread(new Runnable() {
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
                    String sql = "select app_name,app_theme,app_description,meeting_time,em_number,status from bz_meeting_app";

                    try {

                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        ResultSet rSet = preparedStatement.executeQuery();


                        while (rSet.next()){
                            Meeting meeting=new Meeting();
                            meeting.setName(rSet.getString("app_name"));
                            Log.d("sss","搜索"+rSet.getString("app_name"));
                            meeting.setState(rSet.getInt("status"));
                            Log.d("sss","搜"+rSet.getString("status"));
                            meeting.setTime(rSet.getString("meeting_time"));
                            Log.d("sss","搜索"+rSet.getString("meeting_time"));
                            meeting.setNum(rSet.getInt("em_number"));

                            meeting.setDes(rSet.getString("app_description"));
                            meeting.setTheme(rSet.getString("app_theme"));
                            meetingList.add(meeting);
                        }
                        Message msg = new Message();
                        msg.what = 2;
                        uiHandler.sendMessage(msg);


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
        thread.start();
        return true;
    }
}
