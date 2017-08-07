package com.kingbox.holder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kingbox.R;
import com.kingbox.adapter.FieldRVAdapter;
import com.kingbox.adapter.MultiTypeAdapter;
import com.kingbox.service.entity.LiveField;
import com.kingbox.ui.activity.LiveFieldListActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/7/12.
 */

public class LiveFieldViewHolder extends BaseViewHolder<LiveField> {

    /*@BindView(R.id.field_grid_view)
    GridView fieldGridView;*/

    @BindView(R.id.r_v)
    RecyclerView recyclerView;

    //private FieldAdapter fieldAdapter;

    private FieldRVAdapter fieldRVAdapter;

    private List<LiveField> liveFieldList = new ArrayList<>();
    private int screenWidth;//屏幕宽度
    private Context context;

    public LiveFieldViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;

        //toolbar.setSubtitle(isVer ?"GridLayoutManager Vertical":"GridLayoutManager Horizontal");
        GridLayoutManager layoutManager = new GridLayoutManager(context,3);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setHasFixedSize(true);
        fieldRVAdapter = new FieldRVAdapter(context,liveFieldList);
        recyclerView.setAdapter(fieldRVAdapter);

        /*if (null == fieldAdapter) {
            fieldAdapter = new FieldAdapter(context, liveFieldList);
        }
        fieldGridView.setAdapter(fieldAdapter);*/

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        itemView.setLayoutParams(params);

        DisplayMetrics metric = new DisplayMetrics();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);

        screenWidth = metric.widthPixels;
    }

    @Override
    public void setUpView(LiveField model, int position, MultiTypeAdapter adapter) {

        liveFieldList.addAll(model.getLiveFieldList());
        /*ViewGroup.LayoutParams vl = recyclerView.getLayoutParams();
        vl.width = vl.height = screenWidth / 3;*/

        fieldRVAdapter.notifyDataSetChanged();

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
        /*fieldAdapter.notifyDataSetChanged();

        fieldAdapter.setOnItemClickListener(new FieldAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LiveField liveField = liveFieldList.get(position);
                String msg = liveField.getMsg();
                Intent intent = new Intent(context, LiveFieldListActivity.class);
                intent.putExtra("msg", msg);
                context.startActivity(intent);
            }
        });*/
    }
}
