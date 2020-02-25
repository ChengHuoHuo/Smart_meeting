package com.example.yihuier_phone.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.yihuier_phone.Entity.Meeting;
import com.example.yihuier_phone.R;

import java.util.List;

public class MeetingAdapter extends ArrayAdapter<Meeting> {

    private int resourceId;


    public MeetingAdapter(Context context, int textViewResourceId, List<Meeting> objects) {
        super(context, textViewResourceId, objects);

        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Meeting user = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView name;

      ImageView state;


        TextView time;
        View fruitView;

state=(ImageView)view.findViewById(R.id.state1);


        name = (TextView) view.findViewById(R.id.name1);

        time = (TextView) view.findViewById(R.id.time1);

        name.setText(user.getName());

        if(user.getState()==1){
            state.setImageResource(R.drawable.redpoint);}
        else
        {
            state.setImageResource(R.drawable.alread);

        }
        time.setText(user.getTime());


        return  view;

    }  }