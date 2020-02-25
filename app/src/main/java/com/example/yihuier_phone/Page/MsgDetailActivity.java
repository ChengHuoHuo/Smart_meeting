package com.example.yihuier_phone.Page;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yihuier_phone.MsgActivity;
import com.example.yihuier_phone.R;
import com.example.yihuier_phone.TabLayoutTopActivity;

public class MsgDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_detail);
        TextView name;
        TextView theme;
        TextView des;
        TextView num;
        TextView content;
        TextView time;
        content= (TextView) findViewById(R.id.msgcontent);
       des= (TextView) findViewById(R.id.msgdescription);
       num= (TextView) findViewById(R.id.msgnum);
        name = (TextView) findViewById(R.id.msgname);
        theme=(TextView)findViewById(R.id.msgtheme) ;
        time = (TextView) findViewById(R.id.msgtime);
        final Intent intent= getIntent(); //获取传递过来的intent
        Bundle bundle=intent.getExtras();
        name.setText(bundle.getString("name"));
        des.setText(bundle.getString("des"));
        theme.setText(bundle.getString("theme"));
        num.setText(bundle.getInt("num")+"人");
        Log.d("qq","搜索"+bundle.getInt("num"));
        time.setText(bundle.getString("time"));


        ImageView imageView=(ImageView)findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MsgDetailActivity.this,TabLayoutTopActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("status",1);
                startActivity(intent);
            }
        });
        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MsgDetailActivity.this,TabLayoutTopActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("status",1);
                startActivity(intent);
            }
        });
    }

}
