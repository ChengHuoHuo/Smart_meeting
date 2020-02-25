package com.example.yihuier_phone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.yihuier_phone.Adapter.FragmentViewPageAdapter;
import com.example.yihuier_phone.Page.Pagehome;
import com.example.yihuier_phone.Page.Pagemessage;
import com.example.yihuier_phone.Page.Pagemine;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private ViewPager vp_fragment_viewpage;
    private LinearLayout linear_home,linear_message,linear_mine;
    private ImageButton img_home,img_message,img_mine;
    private View Homepage,Messagepage,Minepage;
    private List<Fragment> fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 创建活动时，将其加入管理器中
        ActivityCollectorUtil.addActivity(this);
        fragments=new ArrayList<Fragment>();
        InitView();


    }

    public void InitView(){
        // 初始化 tabs
        vp_fragment_viewpage = (ViewPager) findViewById(R.id.vp_fragment_viewpage);
        linear_home=(LinearLayout)findViewById(R.id.linear_home);
        linear_message=(LinearLayout)findViewById(R.id.linear_message);
        linear_mine=(LinearLayout)findViewById(R.id.linear_mine);

        img_home=(ImageButton)findViewById(R.id.img_home);
        img_message=(ImageButton)findViewById(R.id.img_message);
        img_mine=(ImageButton)findViewById(R.id.img_mine);

        Pagehome pagehome=new Pagehome();
        fragments.add(pagehome);
        Pagemessage pagemessage=new Pagemessage();
        fragments.add(pagemessage);
        Pagemine pagemine=new Pagemine();
        fragments.add(pagemine);


        /**
         * 这里Activity 只有继承 FragmentActivity 的时候 ，才会 getSupportFragmentManager()
         */

        // 设置适配器
        FragmentViewPageAdapter fragmentViewPageAdapter = new FragmentViewPageAdapter(
                getSupportFragmentManager(), fragments);
        vp_fragment_viewpage.setAdapter(fragmentViewPageAdapter);

        linear_home.setOnClickListener(this);
        linear_message.setOnClickListener(this);
        linear_mine.setOnClickListener(this);

        //设置ViewPage 切换效果
        vp_fragment_viewpage.setOnPageChangeListener(new vpOnChangeListener());

    }
    @Override
    public void onClick(View v) {
        ResetTabsImg();
        switch (v.getId()){
            case R.id.linear_home:
                SetTabsSelectedImg(0);
                break;
            case R.id.linear_message:
                SetTabsSelectedImg(1);
                break;
            case R.id.linear_mine:
                SetTabsSelectedImg(2);
                break;
        }
    }

    private void SetTabsSelectedImg(int i) {
        switch (i){
            case 0:
                img_home.setImageResource(R.drawable.home1);
                break;
            case 1:
                img_message.setImageResource(R.drawable.message1);
                break;
            case 2:
                img_mine.setImageResource(R.drawable.mine1);
                break;
        }
        //切换 viewpage item
        vp_fragment_viewpage.setCurrentItem(i);
    }

    private void ResetTabsImg() {
        // 重置tab 图片
        img_home.setImageResource(R.drawable.home);
        img_message.setImageResource(R.drawable.message);
        img_mine.setImageResource(R.drawable.mine);

    }
    class vpOnChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            //设置 tab 背景
            ResetTabsImg();
            SetTabsSelectedImg(position);
        }
    }
}
