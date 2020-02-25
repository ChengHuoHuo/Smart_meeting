package com.example.yihuier_phone.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.yihuier_phone.Question.question;
import com.example.yihuier_phone.R;

import java.util.List;

public class QuestionAdapter extends BaseAdapter {
    private List<question>list;
    private Context context;
    public QuestionAdapter(Context context, List<question> list){
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
            convertView=LayoutInflater.from(context).inflate(R.layout.msg_list_item,null);
            holder.tv_question_id=(TextView)convertView.findViewById(R.id.tv_question_id);
            holder.tv_question_name=(TextView)convertView.findViewById(R.id.tv_question_name);
            holder.tv_question_id.setText(list.get(position).getQuestion_id());
            holder.tv_question_name.setText(list.get(position).getQuestion_name());
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
            holder.tv_question_id=(TextView)convertView.findViewById(R.id.tv_question_id);
            holder.tv_question_name=(TextView)convertView.findViewById(R.id.tv_question_name);
            holder.tv_question_id.setText(list.get(position).getQuestion_id());
            holder.tv_question_name.setText(list.get(position).getQuestion_name());
        }
        return convertView;
    }
    private class ViewHolder{
        TextView tv_question_id;
        TextView tv_question_name;
    }
}
