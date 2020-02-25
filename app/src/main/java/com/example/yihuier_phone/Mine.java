package com.example.yihuier_phone;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Mine extends AppCompatActivity {
    private String str_user_image_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);

        MyApplication app=(MyApplication)getApplication();
        String n=app.getName();
        String a=app.getAccount();

        Bundle bundle=getIntent().getExtras();
        str_user_image_url=bundle.getString("picture_url").toString();
        CircleImageView changeMyInformation=(CircleImageView)findViewById(R.id.user_image);
        Glide.with(this).load(str_user_image_url).into(changeMyInformation);

        TextView tv_name=(TextView)findViewById(R.id.change_name);
        TextView tv_account=(TextView)findViewById(R.id.change_account);
        tv_name.setText(n);
        tv_account.setText(a);

        Button btn_changepassord=(Button)findViewById(R.id.change_password);
        btn_changepassord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changepaasword();
            }
        });

        Button btn_change_phonenum=(Button)findViewById(R.id.change_phonenum);
        btn_change_phonenum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changephonenum();
            }
        });

        Button btn_changeemail=(Button)findViewById(R.id.change_email);
        btn_changeemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeemail();
            }
        });


    }

    public void changepaasword(){
        Intent intent = new Intent(this, resetpassword.class);
        startActivity(intent);
    }

    public void changephonenum(){
        Intent intent = new Intent(this, change_phonenum.class);
        startActivity(intent);
    }

    public void changeemail(){
        Intent intent = new Intent(this, changemail.class);
        startActivity(intent);
    }

    public void goexit(){

    }
}
