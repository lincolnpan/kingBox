package com.kingbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.kingbox.R;
import com.kingbox.service.entity.LiveField;
import com.kingbox.utils.ViewHolder;

import java.util.List;

public class FieldAdapter extends BaseAdapter {

    private Context context;
    private List<LiveField> list;

    public FieldAdapter(Context context, List<LiveField> list) {
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
                    R.layout.field_item, parent, false);
        }

        LiveField liveField = list.get(position);
        String msg = liveField.getMsg();
        int index = msg.indexOf("@tp");
        int end = msg.indexOf("|@dz");
        msg = msg.substring(index + 3, end);

        /*SimpleDraweeView userImg = ViewHolder.getViewById(convertView, R.id.user_img);
        userImg.setImageURI("http://bee.donewe.com/" + msg);*/

        final View finalConvertView = convertView;
        ViewHolder.getViewById(convertView, R.id.field_item_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(finalConvertView, position);
            }
        });

        return convertView;
    }

    public void setList(List<LiveField> liveFieldList) {
        this.list = liveFieldList;
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
