package com.kingbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kingbox.R;
import com.kingbox.service.entity.Tv;
import com.kingbox.utils.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */
public class TvAdapter extends BaseAdapter {

    private Context context;
    private List<Tv> list;

    public TvAdapter(Context context, List<Tv> list) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.tv_item, parent, false);
        }
        TextView nameTv = ViewHolder.getViewById(convertView, R.id.name_tv);
        ImageView iconImg = ViewHolder.getViewById(convertView, R.id.icon_img);
        Tv tv = list.get(position);
        nameTv.setText(tv.getName());
        Glide.with(context)
                .load(tv.getLogo())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(iconImg);
        //GlideCatchUtil.getInstance().ImageLoading(context, tv.getLogo(), iconImg);
        return convertView;
    }

}
