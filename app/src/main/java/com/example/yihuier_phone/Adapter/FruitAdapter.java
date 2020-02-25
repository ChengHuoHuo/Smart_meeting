package com.example.yihuier_phone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.yihuier_phone.R;

import java.util.List;

public class FruitAdapter extends ArrayAdapter<U> {

    private int resourceId;


    public FruitAdapter(Context context, int textViewResourceId, List<U> objects) {
        super(context, textViewResourceId, objects);

        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        U user = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        TextView name;


     ImageView state;
        TextView time;
        View fruitView;



        name = (TextView) view.findViewById(R.id.name);
        state = (ImageView) view.findViewById(R.id.state);
        time = (TextView) view.findViewById(R.id.time);

        name.setText("来自 "+user.getName());
        Log.d("ssss","啊啊"+user.getName());
if(user.getState()==1){
        state.setImageResource(R.drawable.redpoint);}
        else
{
    state.setImageResource(R.drawable.alread);

}        time.setText(user.getTime());


    return  view;

}  }