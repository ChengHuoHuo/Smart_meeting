package com.example.yihuier_phone.Adapter;


public class U {

    private String name;




    private int state;

    private String time;
    private String content;
    private String theme;
    private String des;
    private int num;

    private int msgfromid;

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getMsgfromid() {
        return msgfromid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setMsgfromid(int msgfromid) {
        this.msgfromid = msgfromid;
    }

    public  U(){};
    public U(String name, int state, String time)
    {
        this.name=name;


        this.state=state;
        this.time=time;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getName() {
        return name;

    }

    public void setName(String name1) {
        name=name1;
    }


}