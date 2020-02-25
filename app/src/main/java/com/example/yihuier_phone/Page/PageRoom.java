package com.example.yihuier_phone.Page;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.yihuier_phone.Entity.MyApplication;
import com.example.yihuier_phone.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PageRoom extends Fragment {
    private SearchView mSearchView;
    private ListView mListView;
    List<String> list;
    ArrayAdapter<String> adapter;
    private Connection conn=null;
    private String k;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.roomlayout, container, false);

        mSearchView = (SearchView)view.findViewById(R.id.searchView);
        mListView = (ListView)view.findViewById(R.id.search_listView);
        list = new ArrayList<String>();

        // 设置搜索文本监听


            // 当搜索内容改变时触发该方法


                readData(list);
        for(int i = 0;i < list.size(); i ++){

            Log.e("msffg",  list.get(i));
        }
                adapter=new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, list);
                mListView.setAdapter(adapter);
                mListView.setTextFilterEnabled(true);
                adapter.notifyDataSetChanged();


        return view;
    }
    public void readData(final List<String> list){


        list.clear();
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
                    String sql = "select * from bz_room";

                    try {

                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        ResultSet rSet = preparedStatement.executeQuery();
                        String[] strss={"1111","2222","3333"};

                        while (rSet.next()){
                            list.add(rSet.getString("room_name"));

                        }


                      /*  myApplication=new MyApplication();
                        myApplication.setScore(strss);
                        String[] sss = myApplication.getScore();
                        Log.e("msfg", sss[2]);*/
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

    }

}
