package com.kingbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingbox.R;
import com.kingbox.service.entity.LiveField;
import com.kingbox.utils.GlideCatchUtil;
import com.kingbox.utils.ViewHolder;

import java.util.List;

/**
 * Created by Administrator on 2017/7/12.
 */
public class LiveFieldListAdapter extends BaseAdapter {

    private Context context;
    private List<LiveField> list;

    public LiveFieldListAdapter(Context context, List<LiveField> list) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.field_list_item, parent, false);
        }
        TextView nameTv = ViewHolder.getViewById(convertView, R.id.name_tv);
        ImageView userImg = ViewHolder.getViewById(convertView, R.id.user_img);

        LiveField liveField = list.get(position);
        String msg = liveField.getMsg();
        int index = msg.indexOf("@tp");
        int end = msg.indexOf("@dz");
        int a = msg.indexOf("@mc");
        String name;
        try {
            name = msg.substring(a + 3, index - 1);
        } catch (Exception e) {
            name = "";
        }
        nameTv.setText(name);
        String url;
        try {
            url = msg.substring(index + 3, end - 1);
        } catch (Exception e) {
            url = "";
        }

        GlideCatchUtil.getInstance().ImageLoading(context, url, userImg, R.drawable.df);
        //userImg.setImageURI(url);

        final View finalConvertView = convertView;
        LinearLayout fieldItemLayout = ViewHolder.getViewById(convertView, R.id.field_list_item_layout);

        fieldItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(finalConvertView, position);
            }
        });

        return convertView;
    }

    /**
     * item点击监听接口
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    /**
     * item点击监听
     */
    private OnItemClickListener onItemClickListener;

    /**
     * 设置item点击事件
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}