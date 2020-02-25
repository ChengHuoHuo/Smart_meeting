package com.example.yihuier_phone.Entity;

public class Meeting {
    private String name;


    private int state;

    private String time;
    private String content;
    private String theme;
    private String des;
    private int num;
public Meeting(){};
public Meeting(  String name,int state,String time,String content, String theme, String des,int num)
{
    this.name=name;
    this.state=state;
    this.time=time;
    this.content=content;
    this.theme=theme;
    this.des=des;
this.num=num;

};
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

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

    public void setNum(int  num) {
        this.num = num;
    }
}
