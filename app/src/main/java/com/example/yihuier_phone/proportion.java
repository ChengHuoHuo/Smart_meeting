package com.example.yihuier_phone;

public class proportion {
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getProportion() {
        return proportion;
    }

    public void setProportion(double proportion) {
        this.proportion = proportion;
    }

    double proportion;
    public proportion(){}
    public proportion(String name,double proportion){
        this.name=name;
        this.proportion=proportion;
    }
}
