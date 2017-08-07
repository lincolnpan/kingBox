package com.kingbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kingbox.R;
import com.kingbox.service.entity.LiveField;
import com.kingbox.ui.activity.LiveFieldListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/19.
 */
public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.VHolder> {

    private List<LiveField> list = new ArrayList<>();

    private Context context;

    private FieldRVAdapter fieldRVAdapter;

    private List<LiveField> liveFieldList = new ArrayList<>();  // 子数据

    public ChannelAdapter(Context context, List<LiveField> liveFieldList){
        this.context = context;
        this.list = liveFieldList;
    }

    @Override
    public VHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.channel_item, parent,
                false);
        return new VHolder(view);
    }

    @Override
    public void onBindViewHolder(VHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        if (null == list) {
            return 0;
        }
        return list.size();
    }

    class VHolder extends RecyclerView.ViewHolder{
        RecyclerView rv;
        public VHolder(View itemView) {
            super(itemView);
            rv= (RecyclerView) itemView.findViewById(R.id.r_v);

            GridLayoutManager layoutManager = new GridLayoutManager(context,3);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            rv.setLayoutManager(layoutManager);

            rv.setHasFixedSize(true);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemView.setLayoutParams(params);
        }

        public void bindData(int position){

            LiveField liveField = list.get(position);
            liveFieldList.clear();
            liveFieldList.addAll(liveField.getLiveFieldList());

            fieldRVAdapter = new FieldRVAdapter(context,liveFieldList);
            rv.setAdapter(fieldRVAdapter);
            fieldRVAdapter.setOnItemClickListener(new FieldRVAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    LiveField liveField = liveFieldList.get(position);
                    String msg = liveField.getMsg();
                    Intent intent = new Intent(context, LiveFieldListActivity.class);
                    intent.putExtra("msg", msg);
                    context.startActivity(intent);
                }
            });

            fieldRVAdapter.notifyDataSetChanged();


        }
    }
}
