package com.example.petrica.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.petrica.R;
import com.example.petrica.model.Comment;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentAdapter extends MainAdapter<Comment>{

    public CommentAdapter(List<Comment> data, LayoutInflater li) {
        super(data, li);
    }

    @Override
    public String getObjectId(int position) {
        return data.get(position).getId_comment();
    }

    static class ViewHolder{
        public TextView info;
        public TextView content;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null){
            // inflate the view
            convertView = li.inflate(R.layout.comment_list_item,parent,false);
            vh = new ViewHolder();
            vh.info = convertView.findViewById(R.id.label_info);
            vh.content = convertView.findViewById(R.id.label_content);
            convertView.setTag(vh);
        }
        else{
            vh = (ViewHolder) convertView.getTag();
        }
        Comment c = data.get(position);
        vh.content.setText(c.getMessage());
        vh.info.setText(c.getName_user()+" ("+SimpleDateFormat.getDateInstance().format(c.getDate())+")");
        return convertView;
    }
}
