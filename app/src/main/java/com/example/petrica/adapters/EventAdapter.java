package com.example.petrica.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.petrica.R;
import com.example.petrica.model.Event;
import com.google.firebase.storage.FirebaseStorage;

import java.text.DateFormat;
import java.util.List;

public class EventAdapter extends MainAdapter<Event>{

    public EventAdapter(List<Event> data, LayoutInflater li) {
        super(data, li);
    }

    @Override
    public String getObjectId(int position) {
        return data.get(position).getId_event();
    }

    static class ViewHolder{
        public ImageView image;
        public TextView title;
        public TextView theme;
        public TextView date;
        public ProgressBar progress;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null){
            // inflate the view
            convertView = li.inflate(R.layout.event_list_item,parent,false);
            vh = new ViewHolder();
            vh.image = convertView.findViewById(R.id.image);
            vh.date = convertView.findViewById(R.id.date);
            vh.theme = convertView.findViewById(R.id.theme);
            vh.title = convertView.findViewById(R.id.title);
            vh.progress = convertView.findViewById(R.id.progress);
            convertView.setTag(vh);
        }
        else{
            vh = (ViewHolder) convertView.getTag();
        }
        Event e = (Event) getItem(position);
        DateFormat df = DateFormat.getDateInstance();
        vh.date.setText(df.format(e.getDate()));
        vh.theme.setText(e.getTheme());
        vh.title.setText(e.getName());
        // During the wait for downloading the picture, a progress bar is shown instead
        vh.image.setVisibility(View.INVISIBLE);
        vh.progress.setVisibility(View.VISIBLE);
        vh.progress.setIndeterminate(true);
        final ProgressBar progressBar = vh.progress;
        final ImageView imageView = vh.image;
        Glide.with(vh.image) // Download picture
                .load(FirebaseStorage.getInstance().getReference().child(e.getImage_path()))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(vh.image);
        return convertView;
    }
}
