package com.example.yihuier_phone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Notes_show extends AppCompatActivity {

    private ListView lv_note;
    String u_account;
    private Connection conn=null;
    private List<Map<String, Object>> data_note;
    SimpleAdapter adapter;
    private TextView tv_note_id;
    private String str;
    private String str1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_show);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);

        final MyApplication app=(MyApplication)getApplication();
        u_account=app.getAccount();
        Bundle bundle=getIntent().getExtras();
        str=bundle.getString("meeting_id");

        lv_note=(ListView)findViewById(R.id.list_mine_attend_meeting);
        getnotedata(str);

        adapter=new SimpleAdapter(this,data_note,R.layout.notes_list_item,new String[] {"note_id","user_name","user_account"},new int[]{R.id.note_id,R.id.note_user_name,R.id.note_user_account});
        lv_note.setAdapter(adapter);

        lv_note.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_note_id=(TextView)view.findViewById(R.id.note_id);
                str1=tv_note_id.getText().toString();
                Toast.makeText(Notes_show.this, "" + str, Toast.LENGTH_LONG).show();//显示数据

                Intent intent=new Intent(Notes_show.this,Note_show_information.class);
                Bundle bundle=new Bundle();
                bundle.putString("note_id",str1);
                bundle.putString("meeting_id",str);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    public void getnotedata(final String s){
        data_note = new ArrayList<Map<String, Object>>();
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
                    String sql="SELECT user_account,user_name,note_id from bz_user as bu,bz_note as bn where bn.meeting_id=? and bu.user_id=bn.user_id and bu.user_id in (SELECT user_id from bz_note where meeting_id=?);";
                    try {
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setString(1,s);
                        preparedStatement.setString(2,s);
                        ResultSet rSet=preparedStatement.executeQuery();
                        while (rSet.next()){
                            Map map = new HashMap();
                            map.put("note_id",rSet.getInt("note_id"));
                            map.put("user_name",rSet.getString("user_name"));
                            map.put("user_account",rSet.getString("user_account"));
                            data_note.add(map);
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
