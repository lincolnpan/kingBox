package com.kingbox.ui.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.adapter.TvAdapter;
import com.kingbox.service.entity.Tv;
import com.kingbox.ui.activity.VideoPlayerActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 卫视详情列表
 * Created by Administrator on 2017/7/10.
 */
public class TvListFragment extends LazyLoadFragment {

    @BindView(R.id.tv_list_view)
    ListView tvListView;

    private TvAdapter tvAdapter;

    private List<Tv> tvs = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 3333:    // 成功
                    tvs.clear();
                    List<Tv> tempTvs = (List<Tv>) msg.obj;
                    tvs.addAll(tempTvs);

                    // 更新数据
                    tvAdapter.notifyDataSetChanged();
                    break;
                case 4444:    // 失败
                    tvs.clear();
                    // 更新数据
                    tvAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public int getLayout() {
        return R.layout.tv_list_fragment;
    }

    @Override
    public void initViews(View view) {}

    @Override
    public void loadData() {
        if (null == tvAdapter) {
            tvAdapter = new TvAdapter(getActivity(), tvs);
        }
        tvListView.setAdapter(tvAdapter);
        tvAdapter.notifyDataSetChanged();

        tvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Tv tv = tvs.get(position);
                //Intent intent = new Intent(getActivity(), BannerActivity.class);
                Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                intent.putExtra("url", tv.getUrl());
                intent.putExtra("type", "1");
                startActivity(intent);
            }
        });
    }

    public void updateData(int id) {
        getChannl(id);
    }

    private void getChannl(int id) {
        OkHttpUtils.get().url("http://kingbox.donewe.com/API/GetTvChannels?typeId=" + id).id(100)   // 请求Id
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
                                List<Tv> portfolioList = new ArrayList<>();
                                for (int i = 0; i < resultsJson.length(); i++) {
                                    JSONObject resultJson = resultsJson.getJSONObject(i);
                                    Gson gson = new Gson();
                                    Tv banner = gson.fromJson(resultJson.toString(), Tv.class);
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
}
