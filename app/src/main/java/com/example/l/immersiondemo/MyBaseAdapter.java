package com.example.l.immersiondemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class MyBaseAdapter extends BaseAdapter {

    private List<VoiceRecordInfoDb> list;
    private LayoutInflater mInflater;

    public MyBaseAdapter(Context context,List<VoiceRecordInfoDb> list)
    {
        this.list = list;
        mInflater = LayoutInflater.from(context);
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
        View v = mInflater.inflate(R.layout.item,null);

        TextView fileName = v.findViewById(R.id.item_filename);
        TextView fileLength = v.findViewById(R.id.item_musicLen);
        TextView fileDate = v.findViewById(R.id.item_date);

        //get RecordFileBean class

        VoiceRecordInfoDb r = list.get(position);

        //set value
        fileDate.setText(r.getDateInfo());
        fileLength.setText(r.getRecordingTime());
        fileName.setText(r.getFileName());

        return v;
    }
}
