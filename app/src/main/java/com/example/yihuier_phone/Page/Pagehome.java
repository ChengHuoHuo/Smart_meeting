package com.example.yihuier_phone.Page;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.yihuier_phone.Calendar.EventDecorator;
import com.example.yihuier_phone.Calendar.OneDayDecorator;
import com.example.yihuier_phone.ListView.My_Set_ListView;
import com.example.yihuier_phone.Meeting_information_show;
import com.example.yihuier_phone.Meeting_room_information_show;
import com.example.yihuier_phone.MyApplication;
import com.example.yihuier_phone.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class Pagehome extends Fragment {
    //轮播图布局容器
    private FrameLayout fl;
    //轮播图控件
    private ViewPager mViewPaper;
    //轮播图图片集合
    private List<ImageView> images;
    //存放轮播图显示图片
    private String[] imageUris = new String[4];
    //存放轮播图对应id值
    private int[] imageIds = new int[4];
    //存放图片的标题
    private String[] titles = new String[4];
    //轮播图显示文字控件
    private TextView title;
    //轮播图点集合
    private List<View> dots;
    //记录当前跳转的轮播图ID
    private int currentItem;
    //记录上一次点的位置
    private int oldPosition = 0;

    //轮播图适配器
    private ViewPagerAdapter adapter;
    //执行定时任务
    private ScheduledExecutorService scheduledExecutorService;
    private Thread thread;


    private SearchView mSearchView;
    private My_Set_ListView search_listview;
    private My_Set_ListView cListView;
    private  List<Map<String, Object>> list_sesarch;
    private List<Map<String, Object>> list_calendar;
    private TextView tv_id;
    private TextView tv_name_status;
    private String pass_name_id;
    private String get_status;
    SimpleAdapter adapter_list;
    SimpleAdapter adapter_calendar;
    private Connection conn=null;
    private String calen;
    private String k;
    private TextView mTextView;
    private TextView tv_meeting_id;
    private MaterialCalendarView mCalendarView;
    private String pass_meeting_id;
    String u_account;
    private String Old_date;//获取前15天的日期
    private String New_date;//获取后15天的日期
    private List<String> list_getdate;
    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.homelayout, container, false);
        int resourceId = getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int statusBarHeight = getContext().getResources().getDimensionPixelSize(resourceId);//获取系统状态栏高度
            AppBarLayout topView=(AppBarLayout)view.findViewById(R.id.top);
            topView.setPadding(topView.getPaddingLeft(), topView.getPaddingTop() + statusBarHeight, topView.getPaddingRight(), topView.getPaddingBottom());//设置view的paddingTop高度, 防止显示错乱
            topView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        final MyApplication app=(MyApplication)getActivity().getApplication();
        u_account=app.getAccount();
        mTextView=(TextView)view.findViewById(R.id.mTextView);
        mCalendarView=(MaterialCalendarView) view.findViewById(R.id.calendar);
        cListView=(My_Set_ListView)view.findViewById(R.id.offic_show_listView) ;
        list_calendar = new ArrayList<Map<String, Object>>();
        //初始化日期、
        Calendar calender = Calendar.getInstance();
        String datat = calender.get(Calendar.YEAR) + "-" + (calender.get(Calendar.MONTH)+1) + "-" + calender.get(Calendar.DAY_OF_MONTH) ;
        mTextView.setText(datat);
        list_calendar.clear();
        read_calendar(list_calendar,datat);
        adapter_calendar=new SimpleAdapter(getContext(),list_calendar, R.layout.notes_list_item, new String[]{"meeting_id","meeting_name","meeting_time"},new int[]{R.id.note_id,R.id.note_user_name,R.id.note_user_account});
        cListView.setAdapter(adapter_calendar);
        chooseCalender();
        cListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_meeting_id=(TextView)view.findViewById(R.id.note_id);
                pass_meeting_id=tv_meeting_id.getText().toString();
                Intent intent=new Intent(getContext(),Meeting_information_show.class);
                Bundle bundle=new Bundle();
                bundle.putString("meeting_id",pass_meeting_id);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        mSearchView = (SearchView)view.findViewById(R.id.searchView);
        View viewById = mSearchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        if (viewById != null) {
            viewById.setBackgroundColor(Color.TRANSPARENT);
        }

        search_listview = (My_Set_ListView)view.findViewById(R.id.search_listView);
        list_sesarch=new ArrayList<Map<String, Object>>();

        // 设置搜索文本监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText)){
                    //搜索内容发生改变的时候，listview设置为显示
                    ListView lv_searchlist=(ListView)view.findViewById(R.id.search_listView);
                    lv_searchlist.setVisibility(View.VISIBLE);
                    search_listview.clearTextFilter();//过滤掉listview的内容
                    list_sesarch.clear();//将原本的list_search清空
                    readData(list_sesarch,newText);//list_search填充
                    adapter_list=new SimpleAdapter(getContext(),list_sesarch,R.layout.search_list_item,new String[]{"id","status","name"},new int[]{R.id.tv_search_name_id,R.id.tv_search_check_name_status,R.id.tv_search_name});
                    search_listview.setAdapter(adapter_list);//adapter适配
                    adapter_list.notifyDataSetChanged();//search_listview随搜索内容变化而变化
                }
                else {
                    //搜索框为空，设置Listview不显示
                    ListView lv_searchlist=(ListView)view.findViewById(R.id.search_listView);
                    lv_searchlist.setVisibility(View.GONE);
                }
                return false;
            }
        });

        search_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_id=(TextView)view.findViewById(R.id.tv_search_name_id);
                tv_name_status=(TextView)view.findViewById(R.id.tv_search_check_name_status);
                pass_name_id=tv_id.getText().toString();
                get_status=tv_name_status.getText().toString();
                Toast.makeText(getContext(), "获取的name的id=" + pass_name_id+"  获取的状态值="+get_status, Toast.LENGTH_LONG).show();//显示数据
                //判断状态值是否为1，若为1，则查询的是会议信息；若为2，查询的是会议室信息
                if(get_status.equals("1")){
                    //跳转到会议信息显示界面
                    Intent intent=new Intent(getContext(),Meeting_information_show.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("meeting_id",pass_name_id);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
                else {
                    //跳转到会议信息显示界面
                    Intent intent1=new Intent(getContext(),Meeting_room_information_show.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("room_id",pass_name_id);
                    intent1.putExtras(bundle);
                    startActivity(intent1);
                }
            }
        });
        //找到控件
        com.melnykov.fab.FloatingActionButton fab = (com.melnykov.fab.FloatingActionButton)view.findViewById(R.id.fab);
        fab.bringToFront();
        //设置监听
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"FAB clicked",Toast.LENGTH_SHORT).show();
            }
        });

        Old_date=getOldDate(15);
        New_date=getNewDate(15);
        list_getdate=new ArrayList<>();
        getChangeColorDate(list_getdate,Old_date,New_date);
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd");//转化日期格式
        Date d=new Date();
        String now_time=df.format(d);
        mCalendarView.addDecorator(new OneDayDecorator());//设置当前日期样式

        for(int i=0;i<list_getdate.size();i++){
            String value=list_getdate.get(i);
            try {//标点
                if(df.parse(now_time).getTime()>df.parse(value).getTime())
                {//转成long类型比较
                    System.out.println("当前时间大于到期时间");
                    Collection<CalendarDay> dates=new ArrayList<>();
                    dates.add(new CalendarDay(new Date((df.parse(value).getTime()))));
                    mCalendarView.addDecorator(new EventDecorator(Color.GRAY,dates));
                }
                else if(df.parse(now_time).getTime()<df.parse(value).getTime()){
                    System.out.println("当前时间小于到期时间");
                    Collection<CalendarDay> dates=new ArrayList<>();
                    dates.add(new CalendarDay(new Date((df.parse(value).getTime()))));
                    mCalendarView.addDecorator(new EventDecorator(Color.RED,dates));
                }
                else {
                    System.out.println("当前时间等于到期时间");
                    Collection<CalendarDay> dates=new ArrayList<>();
                    dates.add(new CalendarDay(new Date((df.parse(value).getTime()))));
                    mCalendarView.addDecorator(new EventDecorator(Color.GREEN,dates));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        LBT_see(view);

        return view;
    }
    public void LBT_see(View view){
        //新建线程获取轮播图素材
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL pic_url=new URL("http://39.108.10.155:8080/Common/CarouselAdvertisements.json");
                    HttpURLConnection connection = (HttpURLConnection) pic_url.openConnection();
                    connection.setConnectTimeout(5*1000);
                    connection.connect();
                    int code = connection.getResponseCode();
                    Log.d("getLBTImage code ",String.valueOf(code));
                    if(code==200){
                        InputStream inputStream=connection.getInputStream();
                        String result = stremToString(inputStream);
                        JSONObject root_json = new JSONObject(result);//将一个字符串封装成一个json对象root_json
                        Log.d("getLBTImage result ",result.toString());
                        JSONArray jsonArray = root_json.getJSONArray("data");//获取root_json中的data作为jsonArray对象
                        for(int i=0;i<4;i++){
                            JSONObject jsonObject= jsonArray.getJSONObject(i);
                            imageUris[i]=jsonObject.getString("room_pic_url");
                            titles[i]=jsonObject.getString("room_belongs_company_name")+jsonObject.getString("room_name");
                            imageIds[i]=jsonObject.getInt("room_id");
                        }
                    }
                } catch (Exception e) {
                    Log.e("interfaces: ",e.toString());
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fl = (FrameLayout)view.findViewById(R.id.fl);
        //获取屏幕宽高
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        // 屏幕宽度（像素）
        int width = metric.widthPixels;
        Log.d("width ",String.valueOf(width));


        //设置布局容器宽度
        fl.getLayoutParams().width=width;
        //设置布局容器的高度
        fl.getLayoutParams().height=width*2/5;
        //声明轮播图控件
        mViewPaper = (ViewPager) view.findViewById(R.id.vp);

        //初始化显示的图片
        images = new ArrayList<ImageView>();
        //添加图片到图片集合
        for (int i = 0; i < 4; i++) {
            //初始化控件
            ImageView imageView = new ImageView(getContext());
            //设置图片背景
            //这里如果不是在activity上显示轮播图而是fragment的话可以将.with（）里面可以改成fragment
            Glide.with(getContext()).load(imageUris[i]).into(imageView);
            imageView.setId(imageIds[i]);
            //设置点击事件
            imageView.setOnClickListener(new pagerImageOnClick());
            //图片添加到集合
            images.add(imageView);
        }

        //初始化轮播图文字
        title = (TextView)view.findViewById(R.id.title);
        //设置轮播图显示文字
        title.setText(titles[0]);

        //小点集合
        dots = new ArrayList<View>();
        //初始化小点添加到集合
        dots.add(view.findViewById(R.id.dot_0));
        dots.add(view.findViewById(R.id.dot_1));
        dots.add(view.findViewById(R.id.dot_2));
        dots.add(view.findViewById(R.id.dot_3));


        //初始化适配器
        adapter = new ViewPagerAdapter();
        //轮播图绑定适配器
        mViewPaper.setAdapter(adapter);
        //轮播图滑动事件监听
        mViewPaper.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //页面切换后触发
            @Override
            public void onPageSelected(int position) {
                //设置轮播图文字
                title.setText(titles[position]);
                //设置当前小点图片
                dots.get(position).setBackgroundResource(R.drawable.dot_focused);
                //设置前一个小点图片
                dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
                //记录小点id
                oldPosition = position;
                //记录当前位置
                currentItem = position;
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    //将字节流转换为String字符串
    public  static String stremToString(InputStream inputStream) {
        try { //定义一个字节数组输出流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            //定义一个字节数组
            byte[] buffer = new byte[1024];
//                 定义初始长度
            int len = 0;
            while((len = inputStream.read(buffer))!=-1){ //将读的内容写到字节数组输出流中
                outputStream.write(buffer, 0, len); } //将字节输出流转化成字符串
            return outputStream.toString("utf-8");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    //图片点击事件
    private class pagerImageOnClick implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            Toast.makeText(getContext(), String.valueOf(v.getId()), Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(getContext(),Meeting_room_information_show.class);
            Bundle bundle3=new Bundle();
            bundle3.putString("room_id", String.valueOf(v.getId()));
            intent.putExtras(bundle3);
            startActivity(intent);
        }
    }

    // 自定义Adapter
    private class ViewPagerAdapter extends PagerAdapter {
        //返回页卡的数量
        @Override
        public int getCount() {
            return images.size();
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;//官方提示这样写
        }
        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            // TODO Auto-generated method stub
            view.removeView(images.get(position)) ;//删除页卡
        }
        //这个方法用来实例化页卡
        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            // TODO Auto-generated method stub
            //添加图片控件到轮播图控件
            view.addView(images.get(position));
            return images.get(position);
        }
    }
    /**
     * 利用线程池定时执行动画轮播
     */
    ScheduledFuture beeperHandle;
    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        //初始化定时线程
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 设定执行线程计划,初始2s延迟,每次任务完成后延迟2s再执行一次任务
        beeperHandle=scheduledExecutorService.scheduleWithFixedDelay(
                new ViewPageTask(),
                2,
                2,
                TimeUnit.SECONDS);
    }
    private class ViewPageTask implements Runnable {
        @Override
        public void run() {
            currentItem = (currentItem + 1) % imageIds.length;
            //发送消息
            mHandler.sendEmptyMessage(0);
        }
    }


    /**
     * 接收子线程传递过来的数据
     */
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            mViewPaper.setCurrentItem(currentItem);
        };
    };



    public void getChangeColorDate(final List<String> list_getdate, final String old_date, final String new_date){
        final List<String> finalList_getdate = list_getdate;
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
                    String sql_calen="select substring(bmr.meeting_time_start,1,10) from bz_meeting_room as bmr,bz_meeting as bm where bmr.meeting_id=bm.meeting_id and bmr.meeting_time_start between ? and ? and bm.meeting_id in (select meeting_id from bz_meeting_user where user_id=(select user_id from bz_user where user_account=? ));";
                    try {
                        PreparedStatement preparedStatement2 = conn.prepareStatement(sql_calen);
                        preparedStatement2.setString(1,old_date);
                        preparedStatement2.setString(2,new_date);
                        preparedStatement2.setString(3,u_account);
                        ResultSet rSet2=preparedStatement2.executeQuery();
                        while (rSet2.next()){
                            list_getdate.add(rSet2.getString("substring(bmr.meeting_time_start,1,10)"));
                        }
                        rSet2.close();
                        preparedStatement2.close();
                    } catch (SQLException e) {
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
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getOldDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) - distanceDay);
        Date endDate = null;
        try { endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) { e.printStackTrace();
        }
        return dft.format(endDate);
    }
    public static String getNewDate(int distanceDay) {
        SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
        Date beginDate = new Date();
        Calendar date = Calendar.getInstance();
        date.setTime(beginDate);
        date.set(Calendar.DATE, date.get(Calendar.DATE) + distanceDay);
        Date endDate = null;
        try { endDate = dft.parse(dft.format(date.getTime()));
        } catch (ParseException e) { e.printStackTrace();
        }
        return dft.format(endDate);
    }


    public void readData(final List<Map<String, Object>> list, final String s){
        k=s;
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
                    String sql="select bm.meeting_name,bm.meeting_id from bz_meeting as bm,bz_meeting_user as bmu where bm.meeting_name like ?  and bmu.user_id=(select user_id from bz_user where user_account=?) and bmu.meeting_id=bm.meeting_id;";
                    String sql1 = "select room_name,room_id from bz_room where room_name like ?";
                    // 创建用来执行sql语句的对象
                    try {
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        PreparedStatement preparedStatement1 = conn.prepareStatement(sql1);

                        preparedStatement.setString(1,"%"+k+"%");
                        preparedStatement.setString(2,u_account);
                        preparedStatement1.setString(1,"%"+k+"%");

                        ResultSet rSet = preparedStatement.executeQuery();
                        ResultSet rSet1 = preparedStatement1.executeQuery();

                        while (rSet.next()){
                            Map map = new HashMap();
                            map.put("id",rSet.getInt("bm.meeting_id"));
                            map.put("status",1);
                            map.put("name", rSet.getString("bm.meeting_name"));
                            list.add(map);
                        }
                        while (rSet1.next()){
                            Map map = new HashMap();
                            map.put("id",rSet1.getInt("room_id"));
                            map.put("status",2);
                            map.put("name", rSet1.getString("room_name"));
                            list.add(map);
                        }
                        rSet.close();
                        rSet1.close();
                        preparedStatement.close();
                        preparedStatement1.close();

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
    public void read_calendar(final List<Map<String, Object>> list_calendar,final String c){
        calen=c;
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
                    String sql_calen="select bm.meeting_id,substring(bmr.meeting_time_start,12,8),bm.meeting_name from bz_meeting_room as bmr,bz_meeting as bm where bmr.meeting_id=bm.meeting_id and bmr.meeting_time_start like ? and bm.meeting_id in (select meeting_id from bz_meeting_user where user_id=(select user_id from bz_user where user_account=? ));";
                    try {
                        PreparedStatement preparedStatement2 = conn.prepareStatement(sql_calen);
                        preparedStatement2.setString(1,calen+"%");
                        preparedStatement2.setString(2,u_account);
                        ResultSet rSet2=preparedStatement2.executeQuery();
                        while (rSet2.next()){
                            Map map=new HashMap();
                            map.put("meeting_id",rSet2.getInt("bm.meeting_id"));
                            map.put("meeting_name",rSet2.getString("bm.meeting_name"));
                            map.put("meeting_time",rSet2.getString("substring(bmr.meeting_time_start,12,8)"));
                            list_calendar.add(map);
                        }
                        rSet2.close();
                        preparedStatement2.close();
                    } catch (SQLException e) {
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
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 得到选择的日期
     */
    private void chooseCalender() {
        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String dates = date.getYear()+ "-" + ((date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1)) + "-" + (date.getDay() < 10 ? "0" + date.getDay() : date.getDay());
                mTextView.setText(dates);
                list_calendar.clear();
                read_calendar(list_calendar,dates);
                //           adapter_calendar=new SimpleAdapter(getContext(),list_calendar, R.layout.notes_list_item, new String[]{"meeting_id","meeting_name","meeting_time"},new int[]{R.id.note_id,R.id.note_user_name,R.id.note_user_account});
                cListView.setAdapter(adapter_calendar);
                adapter_calendar.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onResume() {
//        currentItem=0;
        super.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();
//        currentItem=0;
        new Thread(new Runnable() {
            public void run() {
                //取消任务
                beeperHandle.cancel(true);
            }
        }).start();
    }
    @Override
    public void onStop() {

        // TODO Auto-generated method stub
        super.onStop();
    }

}