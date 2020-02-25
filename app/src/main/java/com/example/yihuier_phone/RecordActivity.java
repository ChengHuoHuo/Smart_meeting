package com.example.yihuier_phone;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.example.yihuier_phone.interfaces.stremToString;

public class RecordActivity extends AppCompatActivity {
    private String room_pic_url;
    File sdpath;
    private Uri roomUri;

    private byte[] roomByte;

    private Button button;
    File file;
    private Button mBtnRecordAudio;
    private Button mBtnPlayAudio;
    String recorder_path="";
    final static int REQUEST_CODE_RECORDAUDIO=100;
    MediaPlayer mp=new MediaPlayer();
    ArrayList<String> recorderList =new ArrayList<String>();
    class MyFilter implements FilenameFilter {
        private String type;
        public MyFilter(String type){ //构造函数
            this.type = type;
        }
        @Override //实现FilenameFilter接口accept()方法
        public boolean accept(File dir, String name) { //dir当前目录, name文件名
            return name.endsWith(type); //返回true的文件则合格
        }
    }
    private  void getFileFromRecord(){
        recorderList =new ArrayList<String>();
        File sdpath= Environment.getExternalStorageDirectory(); //获得手机SD卡路径
        File path=new File(sdpath+"//SoundRecorder//"); //获得SD卡的recorder文件夹，返回以.3gp结尾的文件 (自定义文件过滤)
        File[ ] songFiles = path.listFiles( new MyFilter(".mp4") );
        for (File file :songFiles){
            recorderList.add(getFileName(file.getAbsolutePath()));
    }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                RecordActivity.this,
                android.R.layout.simple_list_item_single_choice,//单选功能
                recorderList );
        ListView li=(ListView)findViewById(R.id.lv);
        li.setAdapter(adapter);
        li.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                recorder_path=((TextView)view).getText().toString();
                try{
                    mp.reset(); //播放器重置
                    mp.setDataSource(recorder_path);//播放资源文件
                    mp.prepare(); //准备播放
                    mp.start();
                }catch (Exception e){
                    e.printStackTrace();
                }
                mp.start(); //播放
            } //end onItemClick
        }); //end setOnItemClick
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recorder);

        initView();



        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                requestPermissions(new String[]{Manifest.permission.INTERNET},100);

                return;
            }
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.RECORD_AUDIO) != PERMISSION_GRANTED) {
                //如果应用之前请求过此权限但用户拒绝了请求，此方法将返回 true。
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.RECORD_AUDIO)) {
                }
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        0);
            }
        }




        Button bt=(Button)findViewById(R.id.btn1);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFileFromRecord();
            }
        });
        Button bt2=(Button)findViewById(R.id.upload);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialFilePicker()
                        .withActivity(RecordActivity.this)
                        .withRequestCode(10)
                        .start();


            }
        });

        mBtnRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication myApplication=(MyApplication)getApplication();
                myApplication.setMeeting_name("不会也得装会");
                final RecordAudioDialogFragment fragment = RecordAudioDialogFragment.newInstance(150);
                fragment.show(getSupportFragmentManager(), RecordAudioDialogFragment.class.getSimpleName());
                fragment.setOnCancelListener(new RecordAudioDialogFragment.OnAudioCancelListener() {
                    @Override
                    public void onCancel() {
                        fragment.dismiss();
                    }
                });
            }
        });



    }

    private void initView() {
        mBtnRecordAudio = (Button)findViewById(R.id.main_btn_record_sound);

    }
    ProgressDialog progress;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == 10 && resultCode == RESULT_OK){

            progress = new ProgressDialog(RecordActivity.this);
            progress.setTitle("Uploading");
            progress.setMessage("Please wait...");
            progress.show();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    final Map<String, Object> paramMap = new HashMap<String, Object>(); //文本数据全部添加到Map里
                    String boundary="123456";
                    String PREFIX = "--", LINEND = "\r\n";
                    String MULTIPART_FROM_DATA = "multipart/form-data";
                    String CHARSET = "UTF-8";

                    String realPathFromUri ="/sdcard/shumei.txt";


                    String url="http://39.108.10.155:8080/Common/FileUpload/upload.do";
                    //获取到图片
                    try {
                        URL url1=new URL(url);
                        HttpURLConnection connection= (HttpURLConnection) url1.openConnection();
                        connection.setRequestMethod("POST");
                        connection.addRequestProperty("Connection","Keep-Alive");//设置与服务器保持连接
                        connection.addRequestProperty("Charset","UTF-8");//设置字符编码类型
                        connection.addRequestProperty("Content-Type","multipart/form-data;boundary="+boundary);//post请求，上传数据时的编码类型，并且指定了分隔符
                        // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
                        // http正文内，因此需要设为true, 默认情况下是false;
                        connection.setDoOutput(true);
                        //设置是否从httpUrlConnection读入，默认情况下是true;
                        connection.setDoInput(true);
                        // Post 请求不能使用缓存
                        connection.setUseCaches(false);
                        connection.setConnectTimeout(20000);
                        //getOutStream会隐含的进行connect，所以也可以不调用connect
                        DataOutputStream dataOutputStream=new DataOutputStream(connection.getOutputStream());

                        File file=new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));//获取图片所在的文件
                        StringBuilder sb = new StringBuilder(); //用StringBuilder拼接报文，用于上传图片数据
                        sb.append(PREFIX);
                        sb.append(boundary);
                        sb.append(LINEND);
                        sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + LINEND);
                        sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                        sb.append(LINEND);
                        dataOutputStream.write(sb.toString().getBytes());

                        FileInputStream fStream = new FileInputStream(realPathFromUri);
//                            dataOutputStream.writeBytes("file=");

                        byte[] b=new byte[1024];
                        int len=0;
                        while ((len =fStream.read(b))!=-1){
                            dataOutputStream.write(b,0,len);
                        }
                        fStream.close();
                        dataOutputStream.write(LINEND.getBytes());

                        MyApplication myApplication=(MyApplication)getApplication();
                        myApplication.getMeeting_id();
                        paramMap.put("id",1);
                        paramMap.put("type","meeting_recording");
                        StringBuilder text = new StringBuilder();
                        for (Map.Entry<String, Object> entry : paramMap.entrySet()) { //在for循环中拼接报文，上传文本数据
                            text.append("--");
                            text.append(boundary);
                            text.append("\r\n");
                            text.append("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n\r\n");
                            text.append(entry.getValue());
                            text.append("\r\n");
                        }
                        dataOutputStream.write(text.toString().getBytes("utf-8")); //写入文本数据
                        // 请求结束标志
                        byte[] end_data = (PREFIX + boundary+ PREFIX + LINEND).getBytes();
                        dataOutputStream.write(end_data);
                        Log.d("setRoomImage 5 ",text.toString());
                        dataOutputStream.flush();
                        dataOutputStream.close();
                        // 得到响应码
                        int res = connection.getResponseCode();

                    /*if(res==200){//图片更新成功，清除Glide缓存
                        GlideCacheUtil glideCacheUtil=new GlideCacheUtil();
                        glideCacheUtil.clearImageDiskCache(getApplicationContext());
                        glideCacheUtil.clearImageMemoryCache(getApplicationContext());
                        Glide.with(UploadActivity.this).load(room_pic_url)
                                .into(iv_catch_msg);
                    }*/
                        Log.d("setRoomImage 6 code ",String.valueOf(res));
                        Log.d("setRoomImage 7 message ",connection.getResponseMessage());
                        InputStream inputStream=connection.getInputStream();
                        Log.d("setRoomImage 8 ","inputStream start");
//                    if(code==200){
                        String result = stremToString(inputStream);
                        Log.d("setRoomImage 9 ",result.toString());
                        JSONObject root_json = new JSONObject(result);//将一个字符串封装成一个json对象root_json
                        JSONArray jsonArray = root_json.getJSONArray("data");//获取root_json中的data作为jsonArray对象
                        if(jsonArray!=null&&jsonArray.length()>0){
                            JSONObject jsonObject=jsonArray.getJSONObject(0);
                            room_pic_url=jsonObject.getString("path");
                        }
                        inputStream.close();
                        fStream.close();
                        dataOutputStream.close();
                        connection.disconnect();
                    } catch (Exception e) {
                        Log.e("setRoomImage ",e.toString());
//                    return;
                    }








                    progress.dismiss();


                }





            });

            t.start();




        }
    }


    public String getFileName(String pathandname){

        int start=pathandname.lastIndexOf("/");
        int end=pathandname.lastIndexOf(".");
        if(start!=-1 && end!=-1){
            return pathandname.substring(start+1,end);
        }else{
            return null;
        }

    }
}
