package com.example.yihuier_phone.personalinfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yihuier_phone.ActivityCollectorUtil;
import com.example.yihuier_phone.Adapter.QuestionAdapter;
import com.example.yihuier_phone.MainActivity;
import com.example.yihuier_phone.MeetingOrderActivity;
import com.example.yihuier_phone.MyApplication;
import com.example.yihuier_phone.Question.question;
import com.example.yihuier_phone.R;
import com.example.yihuier_phone.User.UserManage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonalMsgActivity extends Activity {
    private final static int CONNECT_OUT_TIME = 5000;
    private String user_id;
    private String phonenum;
    private String Email;
    private String email_checked_message;
    private int numcode;
    private EditText et_email;
    private EditText et_phonenum;
    private EditText et_codenum;
    private Button btn_num_commit;
    private LinearLayout linear_question;
    private Button btn_commit;

    private List<question> msg_question_list;
    private question quest;
    private QuestionAdapter adapter;
    private Spinner sp1;
    private Spinner sp2;
    private Spinner sp3;
    private EditText et_one_answer;
    private EditText et_two_answer;
    private EditText et_three_answer;
    private String first_id;
    private String second_id;
    private String third_id;

    private String commit_message;
    private String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_msg);

        MyApplication app=(MyApplication)getApplication();
        user_id= String.valueOf(app.getUserid());

        showdialog();

        TableRow tablefaceid=(TableRow)findViewById(R.id.faceid);
        tablefaceid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(PersonalMsgActivity.this,FaceActivity.class);
                startActivity(intent);
            }
        });

        //获取6位随机数
        Button btn_get_randomCode=(Button)findViewById(R.id.btn_get_randomCode);
        btn_get_randomCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //电话号码验证
                et_phonenum=(EditText)findViewById(R.id.et_msg_photonum);
                String checked_phonenum=et_phonenum.getText().toString();
                if(isMobileNO(checked_phonenum)){
                    phonenum=checked_phonenum;
                }
                else {
                    Toast.makeText(PersonalMsgActivity.this,"您输入的手机号不存在",Toast.LENGTH_LONG).show();
                }
                //邮箱
                et_email=(EditText)findViewById(R.id.et_msg_email);
                String checked_email=et_email.getText().toString();
                if(isEmail(checked_email)){
                    Email=checked_email;
                    //设置邮箱不可编辑
                    et_email.setFocusable(false);
                    et_email.setFocusableInTouchMode(false);
                }
                else {
                    Toast.makeText(PersonalMsgActivity.this,"您输入的邮箱格式不正确",Toast.LENGTH_LONG).show();
                }
                numcode = (int) ((Math.random() * 9 + 1) * 100000);
                gotochecked_email(Email,numcode);//通过邮箱获取随机数
                Toast.makeText(PersonalMsgActivity.this,email_checked_message,Toast.LENGTH_LONG).show();
                gotocheck_codenum();
            }
        });

    }
    public void showdialog(){
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(this);
        normalDialog.setIcon(R.drawable.logo);
        normalDialog.setTitle("提示");
        normalDialog.setMessage("验证邮箱及验证码正确后才可提交信息哦，亲(-^〇^-) ");
        normalDialog.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        normalDialog.setNegativeButton("关闭",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                    }
                });
        // 显示
        normalDialog.show();
    }
    public void gotocheck_codenum(){
        et_codenum=(EditText)findViewById(R.id.et_check_randomCode);
        btn_num_commit=(Button)findViewById(R.id.btn_check_randomCode);
        btn_num_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String num=et_codenum.getText().toString();
                if(num!=null){
                    String n= String.valueOf(numcode);
                    if(num.equals(n)){
                        linear_question=(LinearLayout)findViewById(R.id.linear_question);
                        linear_question.setVisibility(View.VISIBLE);
                        btn_commit=(Button)findViewById(R.id.btn_commit);
                        btn_commit.setVisibility(View.VISIBLE);
                        goto_questionfill();
                        initquestionspinner();
                        Toast.makeText(PersonalMsgActivity.this,"验证码输入正确",Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(PersonalMsgActivity.this,"验证码输入错误",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(PersonalMsgActivity.this,"验证码输入不能为空",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void initquestionspinner(){
        sp1=(Spinner)findViewById(R.id.spinner_firstquestion);
        sp2=(Spinner)findViewById(R.id.spinner_secondquestion);
        sp3=(Spinner)findViewById(R.id.spinner_thirdquestion);
        adapter=new QuestionAdapter(PersonalMsgActivity.this,msg_question_list);
        sp1.setAdapter(adapter);
        sp2.setAdapter(adapter);
        sp3.setAdapter(adapter);

       sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               String one_id=msg_question_list.get(position).getQuestion_id().toString();
               String one_name=msg_question_list.get(position).getQuestion_name().toString();
               first_id=one_id;
           }
           @Override
           public void onNothingSelected(AdapterView<?> parent) {
               Toast.makeText(PersonalMsgActivity.this,"请选择一个问题",Toast.LENGTH_LONG).show();
           }
       });
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String two_id=msg_question_list.get(position).getQuestion_id().toString();
                String two_name=msg_question_list.get(position).getQuestion_name().toString();
                second_id=two_id;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(PersonalMsgActivity.this,"请选择一个问题",Toast.LENGTH_LONG).show();
            }
        });
        sp3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String three_id=msg_question_list.get(position).getQuestion_id().toString();
                String three_name=msg_question_list.get(position).getQuestion_name().toString();
                third_id=three_id;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(PersonalMsgActivity.this,"请选择一个问题",Toast.LENGTH_LONG).show();
            }
        });


        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_one_answer=(findViewById(R.id.et_answer_one));
                et_two_answer=(EditText)findViewById(R.id.et_answer_two);
                et_three_answer=(EditText)findViewById(R.id.et_answer_three);
                String one_answer=et_one_answer.getText().toString();
                String two_answer=et_two_answer.getText().toString();
                String three_answer=et_three_answer.getText().toString();
                if(one_answer!=null&&two_answer!=null&&three_answer!=null){
                    gotocommit(first_id,one_answer,second_id,two_answer,third_id,three_answer);
                    Toast.makeText(PersonalMsgActivity.this,"meesage="+commit_message,Toast.LENGTH_LONG).show();
                    if(code.equals("0")){
                        Intent intent = new Intent(PersonalMsgActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        
                    }
                }
                else {
                    Toast.makeText(PersonalMsgActivity.this,"答案输入不能为空",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void gotocommit(final String fir_id,final String one_answer,final String sec_id,final String two_answer,final String tid_id,final  String three_answer){
        final String url="http://39.108.10.155:8080/UserManager/Users/perfectingInformation.do";
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
                    //添加post请求的两行属性
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    Log.e("TestDBConn",user_id+"   "+phonenum+"   "+Email+"    "+fir_id+"   "+one_answer+"   "+sec_id+"   "+two_answer+"   "+tid_id+"   "+three_answer);
                    //传递参数
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("user_id", user_id);
                    params.put("phone_number", phonenum);
                    params.put("email",Email );
                    params.put("security_question1", fir_id);
                    params.put("security_answer1",one_answer );
                    params.put("security_question2",sec_id );
                    params.put("security_answer2", two_answer);
                    params.put("security_question3", tid_id);
                    params.put("security_answer3",three_answer );
                    byte[] data = getRequestData(params,"utf-8").toString().getBytes();//获得请求体
                    //设置请求体的长度
                    connection.setRequestProperty("Content-Length", String.valueOf(data.length));
                    //获得输出流，向服务器写入数据
                    OutputStream out = connection.getOutputStream();
                    out.write(data);
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
                    Log.e("TestDBConn",buffer.toString());
                    reader.close();
                    JSONObject obj = new JSONObject( buffer.toString());
                    String message=obj.getString("message");
                    String c=obj.getString("code");
                    commit_message=message;
                    code=c;
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
    public void goto_questionfill(){
        msg_question_list=new ArrayList<>();
        final String url="http://39.108.10.155:8080/UserManager/Users/AllProtectionProblems.json ";
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

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            connection.getInputStream()));
                    final StringBuffer buffer = new StringBuffer();
                    String str1 = null;
                    while ((str1 = reader.readLine()) != null) {
                        buffer.append(str1);
                    }
                    reader.close();
                    JSONObject obj = new JSONObject( buffer.toString());
                    JSONArray user = null;//通过user字段获取其所包含的JSONObject对象
                    user = obj.getJSONArray("data");
                    for(int i=0;i<user.length();i++){
                        quest=new question();
                        JSONObject question_json=user.getJSONObject(i);
                        String ques_name = question_json.getString("question_content");//通过name字段获取其所包含的字符串
                        String ques_id=question_json.getString("question_id");
                        quest.setQuestion_id(ques_id);
                        quest.setQuestion_name(ques_name);
                        msg_question_list.add(quest);
                    }
                    Log.e("TestDBConn","得到的buffer="+buffer.toString());
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
                catch (MalformedURLException e) {
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
    //手机号正则式
    public static boolean isMobileNO(String mobileNums) {
        /**
         * 判断字符串是否符合手机号码格式
         * 移动号段: 134,135,136,137,138,139,147,150,151,152,157,158,159,170,178,182,183,184,187,188
         * 联通号段: 130,131,132,145,155,156,170,171,175,176,185,186
         * 电信号段: 133,149,153,170,173,177,180,181,189
         * @param str
         * @return 待检测的字符串
         */
        String telRegex = "^((13[0-9])|(14[5,7,9])|(15[^4])|(18[0-9])|(17[0,1,3,5,6,7,8]))\\d{8}$";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }
    //邮箱正则式
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    //通过邮箱获取随机码
    public void gotochecked_email(final String email,final int num ){
        Log.e("TestDBConn","邮件="+email+"   随机数="+num);
        final String url="http://39.108.10.155:8080/UserManager/Users/sendEmail.do";
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
                    //添加post请求的两行属性
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    //传递参数
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("mail_account", email);
                    params.put("randomCode", String.valueOf(num));
                    byte[] data = getRequestData(params,"utf-8").toString().getBytes();//获得请求体
                    //设置请求体的长度
                    connection.setRequestProperty("Content-Length", String.valueOf(data.length));
                    //获得输出流，向服务器写入数据
                    OutputStream out = connection.getOutputStream();
                    out.write(data);
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
                    Log.e("TestDBConn",buffer.toString());
                    reader.close();
                    JSONObject obj = new JSONObject( buffer.toString());
                    String message=obj.getString("message");
                    email_checked_message=message;
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
    /*
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     */
    public static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

}
