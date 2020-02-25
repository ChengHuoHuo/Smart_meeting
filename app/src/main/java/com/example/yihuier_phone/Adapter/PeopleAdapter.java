package com.example.yihuier_phone.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yihuier_phone.Entity.People;
import com.example.yihuier_phone.R;
import com.example.yihuier_phone.people_order;

import java.util.List;

public class PeopleAdapter extends ArrayAdapter<People> {

    private int resourceId;


    public PeopleAdapter(Context context, int textViewResourceId, List<People> objects) {
        super(context, textViewResourceId, objects);

        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        People user = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView name;

        name = (TextView) view.findViewById(R.id.name);


        name.setText(user.getName());
        Log.e("ssss","啊啊啊啊啊啊啊啊"+user.getName());



        return  view;

    }  }