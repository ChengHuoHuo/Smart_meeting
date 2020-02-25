package com.example.yihuier_phone.Entity;



import android.app.Application;

/**
 * Created by whs on 17/5/1.
 */

public class MyApplication extends Application {
    private String [] score=new String[]{"777","888","999"};

    public String[] getScore() {
        return score;
    }

    public void setScore(String[] score) {
        this.score = score;
    }
}
