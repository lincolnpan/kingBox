package com.kingbox.ui.fragment;

import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.kingbox.R;
import com.kingbox.adapter.MultiTypeAdapter;
import com.kingbox.service.entity.LiveField;
import com.kingbox.service.entity.Title;
import com.kingbox.service.entity.Visitable;
import com.kingbox.view.PullRecyclerView;
import com.kingbox.view.RecycleViewDivider;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 直播广场
 * Created by Administrator on 2017/7/8.
 */
public class LiveSquareFragment extends LazyLoadFragment {

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    @BindView(R.id.live_square)
    PullRecyclerView recyclerView;

    private MultiTypeAdapter adapter = null;

    private List<Visitable> list = new ArrayList<>();

    public LiveSquareFragment(){}

    @Override
    public int getLayout() {
        return R.layout.live_square_layout;
    }

    @Override
    public void initViews(View view) {


        // 设置布局管理
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 设置item间隔
        recyclerView.addItemDecoration(new RecycleViewDivider(
                getActivity(), LinearLayoutManager.VERTICAL, 12, getResources().getColor(R.color.transparent)));

        //recyclerView.setId(1000);

        swipeRefresh.setColorSchemeResources(R.color.grey);   // 设置下拉刷新样式颜色
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {   // 设置自动刷新
                swipeRefresh.setRefreshing(true);
            }
        });
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {   // 手动下拉刷新
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getGuoWaiData();
                        swipeRefresh.setRefreshing(true);
                    }
                }, 2000);
            }
        });
    }

    LiveField tempLiveField;

    @Override
    public void loadData() {
        getGuoWaiData();
    }

    private void getGuoWaiData() {
        list.add(new Title("国内直播秀场", 2));

        LiveField guoWai = new LiveField(2);
        List<LiveField> l = new ArrayList<>();
        l.add(new LiveField(2));
        guoWai.setLiveFieldList(l);
        list.add(guoWai);

        getGuoNeiData();
    }

    private void getGuoNeiData() {
        OkHttpUtils.get().url("http://bee.donewe.com/yuncaidan.txt")
                .build().execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "yunData.txt")
        {

            @Override
            public void onBefore(Request request, int id) {
            }

            @Override
            public void inProgress(float progress, long total, int id) {
            }

            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(File file, int id) {
                try {
                    List<LiveField> tempList = new ArrayList<>();
                    InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
                    BufferedReader br = new BufferedReader(isr);
                    String readline = "";
                    while ((readline = br.readLine()) != null) {
                        LiveField liveField = new LiveField(2);
                        liveField.setMsg(readline);
                        tempList.add(liveField);
                    }
                    tempList.remove(tempList.size() - 1);
                    br.close();

                    tempLiveField = new LiveField(2);
                    tempLiveField.setLiveFieldList(tempList);

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            list.add(tempLiveField);

                            if (null == adapter) {
                                adapter = new MultiTypeAdapter(list);
                            }
                            recyclerView.setAdapter(adapter);

                            adapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);

                            swipeRefresh.setRefreshing(false);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
       // liveFieldPresenter.onStop();
    }
}
