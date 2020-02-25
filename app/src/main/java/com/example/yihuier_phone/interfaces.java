package com.example.yihuier_phone;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;



public class interfaces {
    private ArrayList<proportion> proportions = new ArrayList<proportion>();
    public interfaces(){}
    public ArrayList<proportion> getDepartmentHistoryProportionList(final int room_id, final int section) {
        final String[] message = {""};
        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url=new URL("http://39.108.10.155:8080/MeetingManager/MeetingAndRooms/topThreeDepartmentRatio?room_id="
                            +URLEncoder.encode(String.valueOf(room_id))
                            +"&section="+URLEncoder.encode(String.valueOf(section)));
                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5*1000);
                    connection.connect();
                    int code = connection.getResponseCode();
                    Log.d("interfaces code ",String.valueOf(code));
                    if(code==200){
                        InputStream inputStream=connection.getInputStream();
                        String result = stremToString(inputStream);
                        JSONObject root_json = new JSONObject(result);//将一个字符串封装成一个json对象root_json
                        Log.d("interfaces result ",result.toString());
                        JSONArray jsonArray = root_json.getJSONArray("data");//获取root_json中的data作为jsonArray对象
                        int codes=root_json.getInt("code");
                        proportions.clear();
                        Log.d("interfaces 0",String.valueOf(jsonArray.length()));
                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject proportion_json=jsonArray.getJSONObject(i);
                            proportion proportion=new proportion(proportion_json.getString("department_name"),proportion_json.getDouble("ratio"));
                            proportions.add(proportion);
                            Log.d("interfaces ",proportion.getName());
                        }
                        Log.d("interfaces 1",String.valueOf(jsonArray.length())+"+"+String.valueOf(proportions.size()));
                        byte[] data=new byte[1024];
                        StringBuffer sb=new StringBuffer();
                        int length=0;
                        while ((length=inputStream.read(data))!=-1){
                            String s=new String(data, Charset.forName("utf-8"));
                            sb.append(s);
                        }
                        message[0] =sb.toString();
                        inputStream.close();
                        connection.disconnect();
                        Log.d("interfaces 2",String.valueOf(jsonArray.length())+"+"+String.valueOf(proportions.size()));

                    }
                } catch (Exception e) {
                    Log.e("interfaces: ",e.toString());
                }
            }

        });
        thread.start();
        Log.d("interfaces 3",String.valueOf(proportions.size()));
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return proportions;
    }


    public boolean isExternalStorageWritable(){
        String state= Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    //下载文件或图片到外部存储
    public void doSDCard(Context context, final int parent_room_id){
        if (isExternalStorageWritable()){
            final Thread thread=new Thread(){
                @Override
                public void run() {
                    try {
                        String boundary="123456";
                        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
                        if (!file.exists()){
                            file.mkdirs();//创建路径
                        }
//                        Map<String ,String> params=new HashMap<>();
//                        params.put("id",String.valueOf(parent_room_id));
//                        params.put("type","meeting_room_pic");
                        File newFile=new File(file.getPath(),System.currentTimeMillis()+".jpg");
//                        newFile.createNewFile();
                        URL url = new URL("http://47.96.104.134/wechat/Public/uploads/huiyishi.jpg");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("POST");
                        connection.setConnectTimeout(5*1000);

                        DataOutputStream dataOutputStream=new DataOutputStream(connection.getOutputStream());


                        String content = "id=" + URLEncoder.encode(String.valueOf(parent_room_id))+"&type=meeting_room_pic";
                        dataOutputStream.writeBytes(content);
//                        try {
//                            Set<String > keySet=params.keySet();
//                            for (String param:keySet){
//                                dataOutputStream.writeBytes(encode(param)+"=");
//                                String value=params.get(param);
//                                dataOutputStream.writeBytes(encode(value));
//                                dataOutputStream.writeBytes(boundary);
//                            }
//                        }catch (Exception e){
//                            Log.e("下载 ",e.toString());
//                        }

                        InputStream inputStream = connection.getInputStream();
                        FileOutputStream fileOutputStream = new FileOutputStream(newFile.getAbsolutePath());
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while ((len = inputStream.read(bytes)) != -1) {
                            fileOutputStream.write(bytes);
                        }
                        inputStream.close();
                        fileOutputStream.close();
                        connection.disconnect();
                    } catch (Exception e) {
                        Log.e("interfaces e:",e.toString());
                    }
                }

            };
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setMessage("外部存储不可用！");
            builder.create().show();
        }
    }
    //上传文件
    public String uploadFile(File file,int parent_room_id){

        String message="";
        String url="http://39.108.10.155:8080/Common/FileUpload/upload.";
        String boundary="123456";
        Map<String ,String> params=new HashMap<>();
        params.put("id",String.valueOf(parent_room_id));
        params.put("type",",meeting_room_pic");
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
            connection.connect();
            int code = connection.getResponseCode();
            Log.d("mmmm",String.valueOf(code));
            //getOutStream会隐含的进行connect，所以也可以不调用connect
            DataOutputStream dataOutputStream=new DataOutputStream(connection.getOutputStream());
            FileInputStream fileInputStream=new FileInputStream(file);
            dataOutputStream.writeBytes("--"+boundary+"\r\n");
            // 设定传送的内容类型是可序列化的java对象
            // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""
                    + URLEncoder.encode(file.getName(),"UTF-8")+"\"\r\n");
            dataOutputStream.writeBytes("\r\n");
            byte[] b=new byte[1024];
            while ((fileInputStream.read(b))!=-1){
                dataOutputStream.write(b);
            }
            dataOutputStream.writeBytes("\r\n");
            dataOutputStream.writeBytes("--"+boundary+"\r\n");
            try {
                Set<String > keySet=params.keySet();
                for (String param:keySet){
                    dataOutputStream.writeBytes("Content-Disposition: form-data; name=\""
                            +encode(param)+"\"\r\n");
                    dataOutputStream.writeBytes("\r\n");
                    String value=params.get(param);
                    dataOutputStream.writeBytes(encode(value)+"\r\n");
                    dataOutputStream.writeBytes("--"+boundary+"\r\n");

                }
            }catch (Exception e){
                Log.e("uploadFile ",e.toString());
            }

            InputStream inputStream=connection.getInputStream();//得到一个输入流（服务端发回的数据
            byte[] data=new byte[1024];
            StringBuffer sb1=new StringBuffer();
            int length=0;
            while ((length=inputStream.read(data))!=-1){
                String s=new String(data, Charset.forName("utf-8"));
                sb1.append(s);
            }
            message=sb1.toString();
            inputStream.close();
            fileInputStream.close();
            dataOutputStream.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    public static String encode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value,"UTF-8");
    }

//    private static class StreamUtil {
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
//    }
}
