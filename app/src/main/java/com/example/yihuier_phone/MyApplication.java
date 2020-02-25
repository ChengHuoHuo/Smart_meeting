package com.example.yihuier_phone;

import android.app.Application;

public class MyApplication extends Application {
    private String account;
    private String name;
    private int userid;
    private String meeting_name;
    private int meeting_id;

    public int getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(int meeting_id) {
        this.meeting_id = meeting_id;
    }

    public void setAccount(String account){
        this.account=account;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setUserid(int userid){this.userid=userid;}
    public String getAccount(){
        return account;
    }
    public String getName(){
        return name;
    }
    public int getUserid() {
        return userid;
    }

    public String getMeeting_name() {
        return meeting_name;
    }

    public void setMeeting_name(String meeting_name) {
        this.meeting_name = meeting_name;
    }
}
