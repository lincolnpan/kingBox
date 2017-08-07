package com.kingbox.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.adapter.TvTypeAdapter;
import com.kingbox.service.entity.Tv;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 卫视直播
 * Created by Administrator on 2017/7/8.
 */
public class StarTVLiveFragment extends LazyLoadFragment {
    @BindView(R.id.list_view)
    ListView listView;

    @BindView(R.id.line_tv)
    TextView line_tv;

    @BindView(R.id.tv_pb)
    ProgressBar tvPb;

    @BindView(R.id.release_load)
    TextView releaseLoad;

    private TvListFragment tvListFragment;

    private TvTypeAdapter tvAdapter;

    private List<Tv> tvs = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1111:    // 成功
                    tvs.clear();
                    List<Tv> tempTvs = (List<Tv>) msg.obj;
                    tvs.addAll(tempTvs);
                    tvAdapter.notifyDataSetChanged();

                    if (null != tvs && tvs.size() > 0) {
                        tvListFragment.updateData(tvs.get(0).getId());
                        line_tv.setVisibility(View.VISIBLE);
                    } else {
                        line_tv.setVisibility(View.GONE);
                        releaseLoad.setVisibility(View.VISIBLE);
                    }
                    tvPb.setVisibility(View.GONE);
                    break;
                case 2222:    // 失败
                    tvPb.setVisibility(View.GONE);
                    releaseLoad.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    public StarTVLiveFragment() {
        super();
    }

    @Override
    public int getLayout() {
        return R.layout.star_tv_live_layout;
    }

    @Override
    public void initViews(View view) {

        if (null == tvListFragment) {
            tvListFragment = new TvListFragment();
        }
        if (!tvListFragment.isAdded()) {
            FragmentManager manager = getChildFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.content_frame_layout, tvListFragment, "");
            transaction.commitAllowingStateLoss();
        }
    }

    @Override
    public void loadData() {
        if (null == tvAdapter) {
            tvAdapter = new TvTypeAdapter(getActivity(), tvs);
        }
        listView.setAdapter(tvAdapter);
        tvAdapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvAdapter.setDefSelect(position);
                tvAdapter.notifyDataSetChanged();
                tvListFragment.updateData(tvs.get(position).getId());
            }
        });

        getData();
    }

    private void getData() {
        OkHttpUtils.get().url("http://kingbox.donewe.com/API/GetAllTvTypes").id(301)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                handler.sendEmptyMessage(2222);
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
                                msg.what = 1111;
                                msg.obj = portfolioList;
                                handler.handleMessage(msg);
                                return;
                            }
                        }
                    }
                } catch (JSONException e) {
                }
                handler.sendEmptyMessage(2222);
                return;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(301);
    }

    @OnClick(R.id.release_load)
    public void onClick() {
        if (tvs.size() == 0) {
            tvPb.setVisibility(View.VISIBLE);
            releaseLoad.setVisibility(View.GONE);
            getData();
        }
    }
}
