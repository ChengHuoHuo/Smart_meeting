package com.example.yihuier_phone.User;

public class UserInfo {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    private String user_id;
    private String user_account;
    private String username;
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getUser_account() {
        return user_account;
    }
    public String getUser_id() {
        return user_id;
    }
    public String getuser_name() {
        return username;
    }
    public String getUserName() {
        return userName;
    }
    public void setuser_name(String username){
        this.username=username;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public void setUser_account(String user_account) {
        this.user_account = user_account;
    }
}
