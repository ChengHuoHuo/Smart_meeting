package com.example.yihuier_phone;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Meeting_Vote extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_vote);
// 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);

    }
}
