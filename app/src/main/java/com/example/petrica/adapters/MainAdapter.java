package com.example.petrica.adapters;

import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class MainAdapter<T> extends BaseAdapter {
    protected List<T> data;
    protected LayoutInflater li;

    public MainAdapter(List<T> data, LayoutInflater li){
        this.data = data;
        this.li = li;
    }

    public void addData(List<T> newData){
        if (newData != null){
            data.addAll(newData);
            notifyDataSetChanged();
        }
    }

    public List<T> getData(){
        return data;
    }

    public void clear(boolean mustNotify) {
        data.clear();
        if (mustNotify){
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
