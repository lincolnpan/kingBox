package com.kingbox.ui.fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.adapter.MultiTypeAdapter;
import com.kingbox.service.entity.Banner;
import com.kingbox.service.entity.LiveField;
import com.kingbox.service.entity.Title;
import com.kingbox.service.entity.Visitable;
import com.kingbox.view.PullBaseView;
import com.kingbox.view.PullRecyclerView;
import com.kingbox.view.RecycleViewDivider;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
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
 * Created by Administrator on 2017/7/14.
 */

public class ForeignFragment extends LazyLoadFragment implements PullBaseView.OnRefreshListener {

    @BindView(R.id.foreign_live)
    PullRecyclerView foreignLive;

    /*@BindView(R.id.foreign_srl)
    SwipeRefreshLayout foreignSrl;*/

    private MultiTypeAdapter adapter = null;

    private List<Visitable> list = new ArrayList();

    private List<LiveField> tempList;

    private LiveField tempLiveField = new LiveField(1);

    private String dir = "http://lyl.donewe.com/abroad/4chan.php?vid=/tag/18/";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3333:
                    List<Banner> banners = (List<Banner>) msg.obj;
                    Title t = new Title("国外直播秀场", 2);
                    t.setBannerList(banners);
                    list.add(t);

                    adapter.notifyDataSetChanged();
                case 4444:
                    getData();
                    break;
                case 1111:    // 处理直播
                    tempLiveField.setLiveFieldList(tempList);

                case 2222:
                    list.add(tempLiveField);
                    adapter.notifyDataSetChanged();
                    //foreignSrl.setRefreshing(false);

                    foreignLive.onHeaderRefreshComplete();
                    break;
            }
        }
    };

    public ForeignFragment() {
    }

    @Override
    public int getLayout() {
        return R.layout.foreign_fragment;
    }

    @Override
    public void initViews(View view) {
        // 设置布局管理
        foreignLive.setLayoutManager(new LinearLayoutManager(getActivity()));
        // 设置item间隔
        foreignLive.addItemDecoration(new RecycleViewDivider(
                getActivity(), LinearLayoutManager.VERTICAL, 12, getResources().getColor(R.color.transparent)));

        //recyclerView.setId(1000);
        foreignLive.setCanScrollAtRereshing(true);
        foreignLive.setOnRefreshListener(this);
        foreignLive.setCanPullDown(true);   // 下拉刷新
        foreignLive.setCanPullUp(false);   // 加载跟多

        if (null == adapter) {
            adapter = new MultiTypeAdapter(list);
        }
        foreignLive.setAdapter(adapter);
        foreignLive.setVisibility(View.VISIBLE);

        getDir();
    }

    @Override
    public void loadData() {
        foreignLive.headerRefreshing();
    }

    private void getBannerData() {

        list.clear();

        OkHttpUtils.get().url("http://kingbox.donewe.com/API/GetAds?typeId=3").tag(203)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(4444);
            }

            @Override
            public void onResponse(String response, int id) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("Success") && jsonObject.getBoolean("Success")) {
                        if (jsonObject.has("Content")) {
                            JSONArray resultsJson = jsonObject.getJSONArray("Content");
                            if (null != resultsJson && resultsJson.length() > 0) {
                                List<Banner> portfolioList = new ArrayList<>();
                                for (int i = 0; i < resultsJson.length(); i++) {
                                    JSONObject resultJson = resultsJson.getJSONObject(i);
                                    Gson gson = new Gson();
                                    Banner banner = gson.fromJson(resultJson.toString(), Banner.class);
                                    portfolioList.add(banner);
                                }
                                Message msg = new Message();
                                msg.what = 3333;
                                msg.obj = portfolioList;
                                handler.handleMessage(msg);
                                return;
                            }
                        }
                    }
                } catch (JSONException e) {
                }
                handler.sendEmptyMessage(4444);
                return;
            }
        });
    }


    private void getDir() {

        OkHttpUtils.get().url("http://kingbox.donewe.com/API/GetForeignPath").tag(208)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dir = "http://lyl.donewe.com/abroad/4chan.php?vid=/tag/18/";
            }

            @Override
            public void onResponse(String response, int id) {
                dir = response;
            }
        });
    }

    private void getData() {
        OkHttpUtils.get().url(dir).tag(204)
                .build().execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "yunData.txt") {

            @Override
            public void onBefore(Request request, int id) {
            }

            @Override
            public void inProgress(float progress, long total, int id) {
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(2222);
            }

            @Override
            public void onResponse(File file, int id) {
                try {
                    tempList = new ArrayList<>();
                    InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
                    BufferedReader br = new BufferedReader(isr);
                    String readline = "";
                    while ((readline = br.readLine()) != null) {
                        if (!readline.contains("无主播在线") && !TextUtils.isEmpty(readline)) {
                            LiveField liveField = new LiveField(1);
                            liveField.setMsg(readline);
                            tempList.add(liveField);
                        }
                    }
                    br.close();
                    if (tempList.size() > 0)
                        tempList.remove(tempList.size() - 1);


                    handler.sendEmptyMessage(1111);
                } catch (Exception e) {
                    handler.sendEmptyMessage(2222);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(203);
        OkHttpUtils.getInstance().cancelTag(204);
    }

    @Override
    public void onHeaderRefresh(PullBaseView view) {
        getBannerData();
    }

    @Override
    public void onFooterRefresh(PullBaseView view) {

    }
}
