package com.example.yihuier_phone.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class UserManage {
    private static UserManage instance;
    private UserManage(){

    }
    public static UserManage getInstance() {
        if (instance == null) {
            instance = new UserManage();
        }
        return instance;
    }
    /**
     * 保存自动登录的用户信息
     */
    public void saveUserInfo(Context context, String username, String password,String user_id,String user_account,String user_name) {
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);//Context.MODE_PRIVATE表示SharePrefences的数据只有自己应用程序能访问。
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("USER_NAME", username);
        editor.putString("PASSWORD", password);
        editor.putString("USERACCOUNT",user_account);
        editor.putString("USER_ID",user_id);
        editor.putString("USERNAME",user_name);
        editor.commit();
    }
    public void clearUserInfo(Context context){
        SharedPreferences.Editor editor =  context.getSharedPreferences("userInfo", Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
    /**
     * 获取用户信息model
     *
     * @param context
     * @param
     * @param
     */
    public UserInfo getUserInfo(Context context) {
        SharedPreferences sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(sp.getString("USER_NAME", ""));
        userInfo.setPassword(sp.getString("PASSWORD", ""));
        userInfo.setUser_id(sp.getString("USER_ID", ""));
        userInfo.setUser_account(sp.getString("USERACCOUNT", ""));
        userInfo.setuser_name(sp.getString("USERNAME", ""));
        return userInfo;
    }
    /**
     * userInfo中是否有数据
     */ public boolean hasUserInfo(Context context) {
         UserInfo userInfo = getUserInfo(context);
         if (userInfo != null) {
             if ((!TextUtils.isEmpty(userInfo.getUserName())) && (!TextUtils.isEmpty(userInfo.getPassword()))&&(!TextUtils.isEmpty(userInfo.getUserName()))&&(!TextUtils.isEmpty(userInfo.getUser_account()))&&(!TextUtils.isEmpty(userInfo.getuser_name()))){
                 //有数据
                 return true;
             } else {
                 return false;
             }
         }
         return false;
     }

}
