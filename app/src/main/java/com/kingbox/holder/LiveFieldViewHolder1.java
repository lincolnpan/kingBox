package com.kingbox.holder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kingbox.R;
import com.kingbox.adapter.FirstLiveAdapter;
import com.kingbox.adapter.MultiTypeAdapter;
import com.kingbox.service.entity.LiveField;
import com.kingbox.ui.activity.BannerActivity;
import com.kingbox.ui.activity.VideoPlayerActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/7/14.
 */
public class LiveFieldViewHolder1 extends BaseViewHolder<LiveField> {

    @BindView(R.id.top_layout)
    RelativeLayout topLayout;

    @BindView(R.id.live_r_v)
    RecyclerView recyclerView;

    @BindView(R.id.hint_tv)
    TextView hintTv;

    private FirstLiveAdapter firstLiveAdapter;

    private List<LiveField> liveFieldList = new ArrayList<>();
    private Context context;

    public LiveFieldViewHolder1(Context context,View itemView) {
        super(itemView);
        this.context = context;
        topLayout.setVisibility(View.GONE);

        GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);

        firstLiveAdapter = new FirstLiveAdapter(context,liveFieldList);
        recyclerView.setAdapter(firstLiveAdapter);


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(params);
    }

    @Override
    public void setUpView(LiveField model, int position, MultiTypeAdapter adapter) {
        List<LiveField> tempList = model.getLiveFieldList();
        if(null != tempList && tempList.size() > 0){
            liveFieldList.clear();
            liveFieldList.addAll(tempList);

            firstLiveAdapter.notifyDataSetChanged();
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            hintTv.setVisibility(View.VISIBLE);
        }
        firstLiveAdapter.setOnItemClickListener(new FirstLiveAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LiveField liveField = liveFieldList.get(position);
                if (liveField.isWebPlay()) {   // 国外
                    Intent intent = new Intent(context, BannerActivity.class);
                    intent.putExtra("url", liveField.getLiveUrl());
                   /* Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra(VideoPlayerActivity.TRANSITION, true);
                    intent.putExtra("url", "rtmp://vid381.naked.com/JBroadcaster/29961_15009597056705/s_1500959731650");*/
                    intent.putExtra("type", liveField.getRoomName());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra(VideoPlayerActivity.TRANSITION, true);
                    intent.putExtra("url", liveField.getLiveUrl());
                    intent.putExtra("imgUrl", liveField.getImg());
                    LiveFieldViewHolder1.this.context.startActivity(intent);
                }
            }
        });

    }
}
