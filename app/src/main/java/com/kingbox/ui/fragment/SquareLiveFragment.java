package com.kingbox.ui.fragment;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kingbox.R;
import com.kingbox.view.RecycleViewDivider;

import butterknife.BindView;

/**
 * Created by lincolnpan on 2017/7/16.
 */

public class SquareLiveFragment extends LazyLoadFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    public int getLayout() {
        return R.layout.square_live_fragment;
    }

    @Override
    public void initViews(View view) {

        // 设置布局管理
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        // 设置item间隔
        recyclerView.addItemDecoration(new RecycleViewDivider(
                getActivity(), LinearLayoutManager.VERTICAL, 12, getResources().getColor(R.color.transparent)));
        //recycler_view.addView();
    }

    @Override
    public void loadData() {

    }
}
