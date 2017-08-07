package com.kingbox.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kingbox.R;
import com.kingbox.adapter.LiveFieldListAdapter;
import com.kingbox.service.entity.LiveField;
import com.kingbox.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

/**
 * 直播秀场列表
 * Created by Administrator on 2017/7/12.
 */
public class LiveFieldListActivity extends BaseActivity {

    @BindView(R.id.back_img)
    ImageView backImg;

    @BindView(R.id.center_title_tv)
    TextView centerTitleTv;

    @BindView(R.id.user_img)
    ImageView userImg;

    @BindView(R.id.field_list_grid_view)
    GridView fieldListGridView;

    @BindView(R.id.sfl)
    SwipeRefreshLayout swipeRefresh;

    private LiveFieldListAdapter liveFieldListAdapter;

    private List<LiveField> liveFieldList = new ArrayList<>();

    private String urlMsg = "";

    private String url;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_field_list);

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
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        getData();
                        swipeRefresh.setRefreshing(true);
                    }
                });
            }
        });

        if (null == liveFieldListAdapter) {
            liveFieldListAdapter = new LiveFieldListAdapter(LiveFieldListActivity.this, liveFieldList);
        }
        fieldListGridView.setAdapter(liveFieldListAdapter);
        liveFieldListAdapter.notifyDataSetChanged();

        liveFieldListAdapter.setOnItemClickListener(new LiveFieldListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LiveField liveField = liveFieldList.get(position);
                String mesg = liveField.getMsg();
                int index = mesg.indexOf("@dz");
                String url;
                try {
                    url = mesg.substring(index + 3, mesg.length() - 1);
                } catch (Exception e){
                    url = "";
                }
                int a = mesg.indexOf("@tp");

                String imgUrl;
                try {
                    imgUrl = mesg.substring(a + 3, index -1);
                } catch (Exception e){
                    imgUrl = "";
                }
                Intent intent = new Intent(LiveFieldListActivity.this, VideoPlayerActivity.class);
                intent.putExtra(VideoPlayerActivity.TRANSITION, true);
                intent.putExtra("url", url);
                intent.putExtra("imgUrl", imgUrl);
                startActivity(intent);
            }
        });

        urlMsg = getIntent().getStringExtra("msg");
        if (TextUtils.isEmpty(urlMsg)) {
            centerTitleTv.setText("国外直播秀场");
            url = "http://lyl.donewe.com/abroad/4chan.php?vid=/tag/18/";

        } else {

            int index = urlMsg.indexOf("@dz");
            int a = urlMsg.indexOf("@mc");
            int b = urlMsg.indexOf("|@tp");
            String title;
            try {
                title = urlMsg.substring(a + 3, b);
            }catch (Exception e){
                title = "";
            }

            centerTitleTv.setText(title);
            urlMsg = urlMsg.substring(index+3, urlMsg.length()-1);
            url = "http://bee.donewe.com/" + urlMsg + ".txt";
        }
        getData();
    }

    @OnClick({R.id.back_img, R.id.user_img})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back_img:
                finish();
                break;
            case R.id.user_img:
                startActivity(new Intent(LiveFieldListActivity.this, LoginActivity.class));
                break;
        }
    }

    public void getData() {
        liveFieldList.clear();

        OkHttpUtils//
                .get()//
                .tag(111)
                .url(url)//
                .build()//
                .execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "11111.txt")//
                {

                    @Override
                    public void onBefore(Request request, int id)
                    {
                    }

                    @Override
                    public void inProgress(float progress, long total, int id)
                    {
                    }

                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                    }

                    @Override
                    public void onResponse(File file, int id)
                    {
                        try {
                            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "GBK");
                            BufferedReader br = new BufferedReader(isr);
                            String readline = "";
                            while ((readline = br.readLine()) != null) {
                                if (!readline.contains("无主播在线") && !TextUtils.isEmpty(readline)) {
                                    LiveField liveField = new LiveField(1);
                                    liveField.setMsg(readline);
                                    liveFieldList.add(liveField);
                                }
                            }
                            br.close();

                            if (liveFieldList.size() > 0)
                                liveFieldList.remove(liveFieldList.size() - 1);

                            swipeRefresh.setRefreshing(false);
                            liveFieldListAdapter.notifyDataSetChanged();

                            if (liveFieldList.size() == 0) {
                                ToastUtils.ToastMessage(LiveFieldListActivity.this, "无主播在线");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        LiveFieldListActivity.this.finish();
                                    }
                                }, 1500);

                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(111);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(LiveFieldListActivity.this).resumeRequests();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Glide.with(LiveFieldListActivity.this).pauseRequests();
    }
}