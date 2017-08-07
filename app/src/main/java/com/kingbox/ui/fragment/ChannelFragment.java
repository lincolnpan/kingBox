package com.kingbox.ui.fragment;

import android.os.Environment;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.kingbox.R;
import com.kingbox.adapter.ChannelAdapter;
import com.kingbox.service.entity.LiveField;
import com.kingbox.utils.ToastUtils;
import com.kingbox.view.RecycleViewDivider;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by Administrator on 2017/7/19.
 */
public class ChannelFragment extends LazyLoadFragment {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefresh;

    private ChannelAdapter channelAdapter;

    private List<LiveField> list = new ArrayList<>();

    private LiveField tempLiveField;

    private String fileUrl = "";

    @Override
    public int getLayout() {
        return R.layout.channel_fragment;
    }

    @Override
    public void initViews(View view) {
        // 设置布局管理
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 设置item间隔
        recyclerView.addItemDecoration(new RecycleViewDivider(
                getActivity(), LinearLayoutManager.VERTICAL, 12, getResources().getColor(R.color.transparent)));

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
                        getData();
                        swipeRefresh.setRefreshing(true);
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void loadData() {
        if (null == channelAdapter) {
            channelAdapter = new ChannelAdapter(getActivity(), list);
        }
        recyclerView.setAdapter(channelAdapter);

        OkHttpUtils.get().url("http://kingbox.donewe.com/API/GetCloudMenuUrl").id(108)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //fileUrl = "http://bee.donewe.com/yuncaidan.txt";
                ToastUtils.ToastMessage(getActivity(), "获取数据失败");
                swipeRefresh.setRefreshing(false);
                //getData();
            }

            @Override
            public void onResponse(String response, int id) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("Success") && jsonObject.getBoolean("Success")) {
                        if (jsonObject.has("Content")) {
                            JSONObject objJson = jsonObject.getJSONObject("Content");
                            if (objJson.has("Value")) {
                                String value = objJson.getString("Value");
                                if (TextUtils.isEmpty(value)) value = "";
                                fileUrl = value;
                                getData();
                                return;
                            }
                        }
                    }
                } catch (JSONException e) {
                }
                //fileUrl = "http://bee.donewe.com/yuncaidan.txt";
                //getData();
                ToastUtils.ToastMessage(getActivity(), "获取数据失败");
                swipeRefresh.setRefreshing(false);
                return;
            }
        });
    }

    private void getData() {
        list.clear();

        OkHttpUtils.get().url(fileUrl)
                .tag(105)
                .build().execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "yunData.txt") {

            @Override
            public void onBefore(Request request, int id) {
            }

            @Override
            public void inProgress(float progress, long total, int id) {
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onResponse(File file, int id) {
                try {
                    List<LiveField> tempList = new ArrayList<>();
                    InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
                    BufferedReader br = new BufferedReader(isr);
                    String readline = "";
                    while ((readline = br.readLine()) != null) {
                        if (readline.contains("@tp")){

                            LiveField liveField = new LiveField(2);
                            liveField.setMsg(readline);
                            tempList.add(liveField);
                        }
                    }
                    if (tempList.size() > 0) {
                        tempList.remove(tempList.size() - 1);
                    }
                    br.close();

                    tempLiveField = new LiveField(2);
                    tempLiveField.setLiveFieldList(tempList);

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            list.add(tempLiveField);  //===========

                            channelAdapter.notifyDataSetChanged();
                            recyclerView.setVisibility(View.VISIBLE);

                            swipeRefresh.setRefreshing(false);
                        }
                    });

                } catch (Exception e) {
                    swipeRefresh.setRefreshing(false);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(105);
    }
}
