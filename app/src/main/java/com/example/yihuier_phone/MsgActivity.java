package com.example.yihuier_phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yihuier_phone.Adapter.FruitAdapter;

public class MsgActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        TextView name;
        TextView msgfromid;
        TextView content;
        TextView time;
        content= (TextView) findViewById(R.id.msgcontent);

        name = (TextView) findViewById(R.id.msgname);
        msgfromid=(TextView)findViewById(R.id.msgfromid) ;
        time = (TextView) findViewById(R.id.msgtime);
        final Intent intent= getIntent(); //获取传递过来的intent
        Bundle bundle=intent.getExtras();
        name.setText(bundle.getString("name"));

        time.setText(bundle.getString("time"));
        content.setText(bundle.getString("content"));
        ImageView imageView=(ImageView)findViewById(R.id.back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MsgActivity.this,TabLayoutTopActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("status",1);
                intent.putExtras(bundle); // 为intent 设置bundle
                startActivity(intent);
            }
        });
        Button button=(Button)findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MsgActivity.this,TabLayoutTopActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("status",1);
                intent.putExtras(bundle); // 为intent 设置bundle
                startActivity(intent);
            }
        });

    }

}
