package com.example.yihuier_phone;

import android.content.ClipData;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


import com.example.yihuier_phone.Adapter.U;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifeng on 16/8/3.
 *
 */
public class TabLayoutTopActivity extends BaseActivity {

    private TabLayout mTabTl;
    private ViewPager mContentVp;

    private List<String> tabIndicators;
    private List<Fragment> tabFragments;
    private ContentPagerAdapter contentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout_top);

        mTabTl = (TabLayout) findViewById(R.id.tl_tab);
        mContentVp = (ViewPager) findViewById(R.id.vp_content);

        initContent();
        initTab();
    }

    private void initTab(){
        mTabTl.setTabMode(TabLayout.MODE_SCROLLABLE);
        mTabTl.setTabTextColors(ContextCompat.getColor(this, R.color.white), ContextCompat.getColor(this, R.color.yellow));
        mTabTl.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.yellow));

        ViewCompat.setElevation(mTabTl, 10);
        mTabTl.setupWithViewPager(mContentVp);
    }

    private void initContent(){
        tabIndicators = new ArrayList<>();

            tabIndicators.add("管理员通知" );
        tabIndicators.add("会议详情" );
        tabFragments = new ArrayList<>();
        for (String s : tabIndicators) {
            tabFragments.add(TabContentFragment.newInstance(s));
        }
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        mContentVp.setAdapter(contentAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tab_layout, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tab_add:
                tabIndicators.add("Tab " + tabIndicators.size());
                tabFragments.add(TabContentFragment.newInstance(tabIndicators.get(tabIndicators.size()-1)));
                contentAdapter.notifyDataSetChanged();
                mTabTl.setupWithViewPager(mContentVp);
                return true;

            case R.id.tab_mode_fixed:
                mTabTl.setTabMode(TabLayout.MODE_FIXED);
                U u =new U();
                u.setState(0);


                return true;

            case R.id.tab_mode_scrollable:
                mTabTl.setTabMode(TabLayout.MODE_SCROLLABLE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ContentPagerAdapter extends FragmentPagerAdapter{

        public ContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new TabContentFragment();
                case 1:
                    return new tabcontentmsgfragment();

            }
            return null;
        }


        @Override
        public int getCount() {
            return tabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabIndicators.get(position);
        }

    }

}
