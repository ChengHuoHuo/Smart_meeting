package com.example.yihuier_phone;

public class people_order {
    private String department_name;
    private  int user_id;
    private  String name;

    public String getDepartment_name() {
        return department_name;
    }

    public void setDepartment_name(String department_name) {
        this.department_name = department_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public people_order(String department_name, int user_id, String name)
    {
        this.department_name=department_name;
        this.user_id=user_id;
        this.name=name;
    }
    public people_order(){}
}
