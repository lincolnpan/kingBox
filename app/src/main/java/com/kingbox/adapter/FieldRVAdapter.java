package com.kingbox.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingbox.R;
import com.kingbox.service.entity.LiveField;
import com.kingbox.utils.GlideCatchUtil;

import java.util.List;

/**
 * Created by Administrator on 2017/7/13.
 */

public class FieldRVAdapter extends RecyclerView.Adapter<FieldRVAdapter.ListHolder> {

    private Context context;

    private List<LiveField> list;
    public FieldRVAdapter(Context context, List<LiveField> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.field_item, parent, false);

        return new ListHolder(view);
    }

    @Override
    public void onBindViewHolder(ListHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        if (null == list)
            return 0;
        return list.size();
    }

    class ListHolder extends RecyclerView.ViewHolder {
        ImageView userImg;
        LinearLayout fieldItemLayout;
        TextView nameTv;

        public ListHolder(View itemView) {
            super(itemView);
            userImg = (ImageView) itemView.findViewById(R.id.user_img);
            nameTv = (TextView) itemView.findViewById(R.id.name_tv);
            fieldItemLayout = (LinearLayout) itemView.findViewById (R.id.field_item_layout);
        }

        public void setData(final int position){

            LiveField liveField = list.get(position);
            String msg = liveField.getMsg();
            if (!TextUtils.isEmpty(msg)) {
                int index = msg.indexOf("@tp");
                int end = msg.indexOf("|@dz");
                int a = msg.indexOf("@mc");
                String name;
                try {
                    name= msg.substring(a + 3, index-1);
                } catch (Exception e){
                    name = "";
                }
                nameTv.setText(name);
                try {
                    msg = msg.substring(index + 3, end);
                } catch (Exception e){
                    msg = "";
                }

                GlideCatchUtil.getInstance().ImageLoading(context, "http://bee.donewe.com/" + msg, userImg);
                //userImg.setImageURI("http://bee.donewe.com/" + msg);
            } else {
                nameTv.setText("国外直播秀场");
            }

            itemView.findViewById (R.id.field_item_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(ListHolder.this.itemView, position);
                }
            });
        }
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
