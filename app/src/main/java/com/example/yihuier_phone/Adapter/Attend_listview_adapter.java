package com.example.yihuier_phone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.yihuier_phone.People_attend;
import com.example.yihuier_phone.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Attend_listview_adapter extends BaseAdapter {
    private List<People_attend> list;
    private Context context;

    public Attend_listview_adapter(Context context, List<People_attend> list){
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
            convertView=LayoutInflater.from(context).inflate(R.layout.attend_people_item,null);
            holder.iv_list= (CircleImageView) convertView.findViewById(R.id.attend_people_image);
            holder.tv_list=(TextView)convertView.findViewById(R.id.tv_attend_people_name);
            holder.tv_list.setText(list.get(position).getTextTitle());
            Glide.with(context)
                    .load(list.get(position).getUrl())
                    .centerCrop()
                    .into(holder.iv_list);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
            holder.iv_list= (CircleImageView) convertView.findViewById(R.id.attend_people_image);
            holder.tv_list=(TextView)convertView.findViewById(R.id.tv_attend_people_name);
            holder.tv_list.setText(list.get(position).getTextTitle());
            Glide.with(context)
                    .load(list.get(position).getUrl())
                    .centerCrop()
                    .into(holder.iv_list);
        }
        return convertView;
    }
    private class ViewHolder{
        CircleImageView iv_list;
        TextView tv_url;
        TextView tv_list;
    }

}
