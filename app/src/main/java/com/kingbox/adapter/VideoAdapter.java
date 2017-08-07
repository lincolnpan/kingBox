package com.kingbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingbox.R;
import com.kingbox.service.entity.Cinema;
import com.kingbox.utils.GlideCatchUtil;
import com.kingbox.utils.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */
public class VideoAdapter extends BaseAdapter {

    private Context context;
    private List<Cinema> list;
    public VideoAdapter(Context context, List<Cinema> list){
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
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.void_item, parent, false);
        }
        TextView nameTv = ViewHolder.getViewById(convertView, R.id.name_tv);
        ImageView iconImg = ViewHolder.getViewById(convertView, R.id.icon_img);

        Cinema cinema = list.get(position);

        GlideCatchUtil.getInstance().ImageLoading(context, cinema.getLogo(), iconImg);
        //iconImg.setImageURI(cinema.getLogo());

        nameTv.setText(cinema.getName());

        return convertView;
    }
}
