package com.kingbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kingbox.R;
import com.kingbox.service.entity.Tv;
import com.kingbox.utils.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/7/26.
 */

public class TvTypeAdapter extends BaseAdapter {

    private Context context;
    private List<Tv> list;
    private int defItem = 0;//声明默认选中的项

    public TvTypeAdapter(Context context, List<Tv> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        if (null == list) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return "";
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     适配器中添加这个方法
     */
    public void setDefSelect(int position) {
        this.defItem = position;
        //notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.tv_type_item, parent, false);
        }
        TextView nameTv = ViewHolder.getViewById(convertView, R.id.name_tv);

        if (defItem == position) {
            convertView.setBackgroundResource(R.drawable.select_tv_icon);
            nameTv.setTextColor(0XFF13BAA6);
        } else {
            convertView.setBackgroundResource(R.drawable.tv_bg);
            nameTv.setTextColor(0XFF1F1D20);
        }

        Tv tv = list.get(position);
        nameTv.setText(tv.getName());

        return convertView;
    }
}
