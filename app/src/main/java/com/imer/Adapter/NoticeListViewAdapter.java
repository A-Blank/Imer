package com.imer.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imer.Bean.Notice;
import com.imer.R;

import java.util.List;

/**
 * Created by 丶 on 2017/2/23.
 */

public class NoticeListViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Notice> mDatas;
    private LayoutInflater mInflater;

    public NoticeListViewAdapter(Context context, List<Notice> Datas){

            this.mContext=context;
            this.mDatas=Datas;
            mInflater=LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder=new ViewHolder();
            convertView=mInflater.inflate(R.layout.item_notice,parent, false);
            viewHolder.title=(TextView) convertView.findViewById(R.id.tv_notice_title);
            viewHolder.content= (TextView) convertView.findViewById(R.id.tv_notice_content);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder= (ViewHolder) convertView.getTag();
        }

        if(mDatas.get(position).getType().equals("system")){
            viewHolder.title.setTextColor(Color.RED);
        }

        viewHolder.title.setText(mDatas.get(position).getTitle());
        viewHolder.content.setText(mDatas.get(position).getContent());

        return convertView;
    }


    class ViewHolder{

        TextView title;
        TextView content;

    }

}
