package com.example.yihuier_phone.personalinfo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.FileProvider;
import android.support.v4.database.CursorWindowCompat;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.yihuier_phone.MyApplication;
import com.example.yihuier_phone.R;
import com.example.yihuier_phone.RealPathFromUriUtils;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;

import java.io.DataOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class FaceActivity extends Activity {
    private final static int CONNECT_OUT_TIME = 5000;
    private static final int TIME_OUT =10*1000 ;

    private Button photograph;
    private Button upload;// 上传
    private String Company_id;
    private String userid;
    private ImageView iv;
    public static final int TAKE_PHOTO=1;
    public static final int CHOOSE_PHOTO=2;
    private Uri imageUri;
    private String image_real_path;
    private String Code;
    private String Message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);

        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        MyApplication app=(MyApplication)getApplication();
        userid= String.valueOf(app.getUserid());
        getuser_company(userid);
        init();
        setonlitener();

    }
    public void init(){
        photograph=(Button)findViewById(R.id.btn_userphoto_start);
        iv=(ImageView)findViewById(R.id.iv_user);
        upload=(Button)findViewById(R.id.btn_userphoto_commit);
    }
    public void setonlitener(){
        photograph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(FaceActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(FaceActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else {
                    openAlbum();
                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file=new File(image_real_path);
                uploadFile(file,userid,Company_id);
                if(Code.equals("0")){
                    Toast.makeText(FaceActivity.this,"人脸上传成功"+Message,Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(FaceActivity.this,PersonalMsgActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(FaceActivity.this,"人脸上传失败"+Message,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openCamera(){
        File outputImage=new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/"+System.currentTimeMillis() + ".jpg");
        if (!outputImage.getParentFile().exists()){
            outputImage.getParentFile().mkdirs();
        }
        if (Build.VERSION.SDK_INT >= 24) {
            imageUri=FileProvider.getUriForFile(FaceActivity.this,"com.example.yihuier_phone.fileprovider",outputImage);
        } else {
            imageUri = Uri.fromFile(outputImage);
        }

        Log.e("TestDBConn","    "+imageUri.toString());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }
    private void openAlbum(){
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }
                else {
                    Toast.makeText(this,"你拒绝了权限申请",Toast.LENGTH_LONG).show();
                }
                break;
                default:
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (resultCode == Activity.RESULT_OK) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if(requestCode==RESULT_OK)
                {
                    if(Build.VERSION.SDK_INT>=19){
                        handleImaage(data);
                    }
                } else { handleImagelow(data);
                }
                break;
                default:
                    break;
        }
    }
    }

    @SuppressLint("NewApi")
    public void uploadFile(final File file, final String user_id, final String company_id) {
        final String url="http://119.23.43.142:8080/faceAdd";
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String boundary="123456";
                String PREFIX = "--", LINEND = "\r\n";
                String MULTIPART_FROM_DATA = "multipart/form-data";
                String CHARSET = "UTF-8";
                Map<String ,String> params=new HashMap<>();
                params.put("userId",user_id);
                params.put("groupId",company_id);
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
                    StringBuilder sb = new StringBuilder(); //用StringBuilder拼接报文，用于上传图片数据
                    sb.append(PREFIX);
                    sb.append(boundary);
                    sb.append(LINEND);
                    sb.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getName() + "\"" + LINEND);
                    sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINEND);
                    sb.append(LINEND);
                    dataOutputStream.write(sb.toString().getBytes());
                    FileInputStream fStream = new FileInputStream(image_real_path);
                    byte[] b=new byte[1024];
                    int len=0;
                    while ((len =fStream.read(b))!=-1){
                        dataOutputStream.write(b,0,len);
                    }
                    fStream.close();
                    dataOutputStream.write(LINEND.getBytes());
                    StringBuilder text = new StringBuilder();
                    for (Map.Entry<String, String> entry : params.entrySet()) { //在for循环中拼接报文，上传文本数据
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
                    String code=obj.getString("code");
                    Code=code;
                    Message=name;
                    Log.d("aaa",name);
                    Log.d("bbb",buffer.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public static String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value,"UTF-8");
    }
    public void getuser_company(final String user_id){
        final String url="http://39.108.10.155:8080/UserManager/Users/getPersonalCompanyId.json";
        Thread thread=new Thread(){
            @Override
            public void run() {
                try {
                    // 第一步：创建必要的URL对象
                    URL httpUrl = new URL(url);
                    // 第二步：根据URL对象，获取HttpURLConnection对象
                    HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
                    // 第三步：为HttpURLConnection对象设置必要的参数（是否允许输入数据、连接超时时间、请求方式）
                    connection.setConnectTimeout(CONNECT_OUT_TIME);
                    connection.setReadTimeout(CONNECT_OUT_TIME);
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    String content = "user_id=" + URLEncoder.encode(user_id);// 无论服务器转码与否，这里不需要转码，因为Android系统自动已经转码为utf-8啦
                    //添加post请求的两行属性
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Content-Length", content.length() + "");
                    // 第四步：向服务器写入数据
                    OutputStream out = connection.getOutputStream();
                    out.write(content.getBytes());
                    out.flush();
                    out.close();
                    // 第五步：开始读取服务器返回数据

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    final StringBuffer buffer = new StringBuffer();
                    String str1 = null;
                    while ((str1 = reader.readLine()) != null) {
                        buffer.append(str1);
                    }
                    reader.close();
                    Log.e("TestDBConn",buffer.toString());
                    JSONObject obj = new JSONObject( buffer.toString());
                    String c_id=obj.getString("data");
                    Company_id=c_id;
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @TargetApi(19)
    private void  handleImaage(Intent data) {
        String filePath = null;
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) { // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider // 使用':'分割
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "="+id;
                filePath = getImagePath( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                 Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                 filePath = getImagePath(contentUri, null);
            } } else if ("content".equalsIgnoreCase(uri.getScheme())) { // 如果是 content 类型的 Uri
             filePath = getImagePath( uri, null); }
             else if ("file".equals(uri.getScheme())) { // 如果是 file 类型的 Uri,直接获取图片对应的路径
             filePath = uri.getPath();
        }

        displayImage(filePath);
    }
    private void handleImagelow(Intent data){
        Uri uri=data.getData();
        String filepath=getImagePath(uri,null);
        displayImage(filepath);
    }
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(FaceActivity.this, uri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

//        Cursor cursor =getContentResolver().query(uri, null, selection, null, null);
//            if (cursor != null) {
//                if (cursor.moveToFirst()) {
//                    path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
//                }
//                cursor.close();
//            }
       // return path;
    }
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority()); }

    private void displayImage(String im){

        image_real_path=im;
        Log.e("TestDBConn","image_url   "+image_real_path);
        if(im!=null){
            Bitmap bitmap=BitmapFactory.decodeFile(im);
            iv.setImageBitmap(bitmap);
        }else {
            Toast.makeText(this, "图片损坏，请重新选择", Toast.LENGTH_SHORT).show();
        }
    }



}
