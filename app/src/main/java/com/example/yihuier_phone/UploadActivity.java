package com.example.yihuier_phone;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.example.yihuier_phone.interfaces.stremToString;

public class UploadActivity extends Activity {
    private Uri roomUri;
    private String room_pic_url;
    private byte[] roomByte;

    private Button button;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        button = (Button) findViewById(R.id.button);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                requestPermissions(new String[]{Manifest.permission.INTERNET},100);

                return;
            }
        }

        enable_button();
    }

    private void enable_button() {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(UploadActivity.this)
                        .withRequestCode(10)
                        .start();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100 && (grantResults[0] == PackageManager.PERMISSION_GRANTED)){
            enable_button();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
            }

        }
    }

    ProgressDialog progress;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == 10 && resultCode == RESULT_OK){

            progress = new ProgressDialog(UploadActivity.this);
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
                        paramMap.put("type","meeting_file");
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
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                connection.getInputStream()));
                        final StringBuffer buffer = new StringBuffer();
                        String str1 = null;
                        while ((str1 = reader.readLine()) != null) {
                            buffer.append(str1);
                        }
                        reader.close();

                        JSONObject obj = new JSONObject( buffer.toString());

                        String name = obj.getString("message");
                        Log.d("bbb",name);
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


    }
