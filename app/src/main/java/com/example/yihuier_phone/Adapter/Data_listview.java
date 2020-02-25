package com.example.yihuier_phone.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yihuier_phone.MeetingData;
import com.example.yihuier_phone.R;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Data_listview extends BaseAdapter {
    private List<MeetingData> list;
    private Context context;

    public Data_listview(Context context, List<MeetingData> list){
        this.context=context;
        this.list=list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if (convertView==null){
            holder=new ViewHolder();
            convertView=LayoutInflater.from(context).inflate(R.layout.meeting_data_item,null);
            holder.tv_data_id=(TextView)convertView.findViewById(R.id.tv_meeting_data_id);
            holder.tv_data_name=(TextView)convertView.findViewById(R.id.tv_meeting_data_name);
            holder.tv_data_id.setText(list.get(position).getData_id());
            holder.tv_data_name.setText(list.get(position).getData_name());
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
            holder.tv_data_id=(TextView)convertView.findViewById(R.id.tv_meeting_data_id);
            holder.tv_data_name=(TextView)convertView.findViewById(R.id.tv_meeting_data_name);
            holder.tv_data_id.setText(list.get(position).getData_id());
            holder.tv_data_name.setText(list.get(position).getData_name());
        }
        return convertView;
    }
    private class ViewHolder{
        TextView tv_data_id;
        TextView tv_data_name;
    }
}
