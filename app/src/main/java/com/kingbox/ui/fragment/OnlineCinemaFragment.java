package com.kingbox.ui.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.adapter.BannerPagerAdapter;
import com.kingbox.adapter.VideoAdapter;
import com.kingbox.service.entity.Banner;
import com.kingbox.service.entity.Cinema;
import com.kingbox.ui.activity.BookmarkActivity;
import com.kingbox.ui.activity.HistroyActivity;
import com.kingbox.ui.activity.NoticesActivity;
import com.kingbox.ui.activity.VideoWebViewActivity;
import com.kingbox.utils.GlideCatchUtil;
import com.kingbox.utils.ToastUtils;
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
 * 在线影院
 * Created by Administrator on 2017/7/8.
 */
public class OnlineCinemaFragment extends LazyLoadFragment {

    private List<ImageView> mImageViewList;
    private int currentPosition = 0;
    private int dotPosition = 0;
    private int prePosition = 0;
    private List<ImageView> mImageViewDotList;

    @BindView(R.id.vp_main)
    ViewPager mViewPager;

    @BindView(R.id.ll_main_dot)
    LinearLayout llMainDot;

    @BindView(R.id.video_grid_layout)
    GridView videoGridLayout;

    @BindView(R.id.menu_line_tv)
    TextView menuLineTv;

    @BindView(R.id.cinema_pb)
    ProgressBar cinemaPb;

    @BindView(R.id.cinema_release_load)
    TextView cinemaReleaseLoad;

    @BindView(R.id.menu_layout)
    LinearLayout menuLayout;

    private VideoAdapter videoAdapter;

    private List<Cinema> list = new ArrayList<>();

    List<Banner> banners = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mViewPager.setCurrentItem(currentPosition, false);
                    break;
                case 3333:
                    banners.clear();
                    banners.addAll((List<Banner>) msg.obj);
                    initData();   // 初始化数据
                    setDot();    // 设置位置圆点
                    setViewPager();
                case 4444:
                    GetOnlineMovies();   // 获取影院数据
                    break;
                case 1111:
                    list.clear();
                    list.addAll((List<Cinema>) msg.obj);
                case 2222:
                    if (list.size() > 0) {
                        menuLayout.setVisibility(View.VISIBLE);
                        menuLineTv.setVisibility(View.VISIBLE);
                        videoAdapter.notifyDataSetChanged();
                    } else {
                        menuLayout.setVisibility(View.GONE);
                        menuLineTv.setVisibility(View.GONE);
                        cinemaReleaseLoad.setVisibility(View.VISIBLE);
                    }
                    cinemaPb.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public OnlineCinemaFragment() {
        super();
    }

    @Override
    public int getLayout() {
        return R.layout.online_cinema_layout;
    }

    @Override
    public void initViews(View view) {

        videoGridLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cinema cinema = list.get(position);
                Intent intent = new Intent(getActivity(), VideoWebViewActivity.class);
                intent.putExtra("url", cinema.getAddress());
                startActivity(intent);
            }
        });
    }

    private void getBanner() {
        OkHttpUtils.get().url("http://kingbox.donewe.com/API/GetAds?typeId=2").id(402)   // 请求Id
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

    private void GetOnlineMovies() {
        OkHttpUtils.get().url("http://kingbox.donewe.com/API/GetOnlineMovies").id(401)   // 请求Id
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
                                List<Cinema> portfolioList = new ArrayList<>();
                                for (int i = 0; i < resultsJson.length(); i++) {
                                    JSONObject resultJson = resultsJson.getJSONObject(i);
                                    Gson gson = new Gson();
                                    Cinema banner = gson.fromJson(resultJson.toString(), Cinema.class);
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
    public void loadData() {
        if (null == videoAdapter) {
            videoAdapter = new VideoAdapter(getActivity(), list);
        }
        videoGridLayout.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();

        getBanner();
    }

    @OnClick({R.id.notice_look_img, R.id.bookmark_img, R.id.cache_off_img, R.id.history_look_img, R.id.cinema_release_load})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.notice_look_img:   //观看公告
                //http://kingbox.donewe.com/API/GetNotices
                Intent intent = new Intent(getActivity(), NoticesActivity.class);
                intent.putExtra("type", 2);
                startActivity(intent);
                break;
            case R.id.bookmark_img:   //书签
                startActivity(new Intent(getActivity(), BookmarkActivity.class));
                //ToastUtils.ToastMessage(getActivity(), "暂未开放,敬请期待");
                break;
            case R.id.cache_off_img:   //离线缓存
                ToastUtils.ToastMessage(getActivity(), "暂未开放,敬请期待");
                break;
            case R.id.history_look_img:   // 历史记录
                startActivity(new Intent(getActivity(), HistroyActivity.class));

                break;
            case R.id.cinema_release_load:
                if (list.size() == 0) {
                    cinemaPb.setVisibility(View.VISIBLE);
                    cinemaReleaseLoad.setVisibility(View.GONE);
                    getBanner();
                }
                break;
        }
    }

    private void initData() {
        mImageViewList = new ArrayList<>();
        ImageView imageView;
        for (int i = 0; i < banners.size() + 2; i++) {
            imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            if (i == 0) {   //判断当i=0为该处的ImageView设置最后一张图片作为背景
                GlideCatchUtil.getInstance().ImageLoading(getActivity(), banners.get(banners.size() - 1).getPic(), imageView, R.drawable.banner_df_icon);
                imageView.setOnClickListener(new ImageViewOnclickListener(banners.size() - 1));   //  设置点击事件
                mImageViewList.add(imageView);
            } else if (i == banners.size() + 1) {   //判断当i=images.length+1时为该处的ImageView设置第一张图片作为背景
                GlideCatchUtil.getInstance().ImageLoading(getActivity(), banners.get(0).getPic(), imageView, R.drawable.banner_df_icon);
                imageView.setOnClickListener(new ImageViewOnclickListener(0));   //  设置点击事件
                mImageViewList.add(imageView);
            } else {  //其他情况则为ImageView设置images[i-1]的图片作为背景
                GlideCatchUtil.getInstance().ImageLoading(getActivity(), banners.get(i - 1).getPic(), imageView, R.drawable.banner_df_icon);
                imageView.setOnClickListener(new ImageViewOnclickListener(i - 1));   //  设置点击事件
                mImageViewList.add(imageView);
            }
        }
    }

    //  设置轮播小圆点
    private void setDot() {
        mImageViewDotList = new ArrayList();
        llMainDot.removeAllViews();

        //  设置LinearLayout的子控件的宽高，这里单位是像素。
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(15, 15);
        params.rightMargin = 20;
        //  for循环创建images.length个ImageView（小圆点）
        for (int i = 0; i < banners.size(); i++) {
            ImageView imageViewDot = new ImageView(getActivity());
            imageViewDot.setLayoutParams(params);
            //  设置小圆点的背景为灰色
            imageViewDot.setBackgroundResource(R.drawable.red_dot_night);
            llMainDot.addView(imageViewDot);
            mImageViewDotList.add(imageViewDot);
        }
        //设置第一个小圆点图片背景为白色
        mImageViewDotList.get(dotPosition).setBackgroundResource(R.drawable.red_dot);
    }

    /**
     * banner 点击事件处理
     */
    private class ImageViewOnclickListener implements View.OnClickListener {

        private int position;

        public ImageViewOnclickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Banner banner = banners.get(position);
            Intent intent = new Intent(getActivity(), VideoWebViewActivity.class);
            intent.putExtra("url", banner.getUrl());
            intent.putExtra("histroy", 0);
            startActivity(intent);
        }
    }

    private void setViewPager() {
        BannerPagerAdapter adapter = new BannerPagerAdapter(mImageViewList);
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(currentPosition);

        //页面改变监听
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (null != mImageViewDotList && mImageViewDotList.size() > 0 && null != banners && banners.size() > 0) {
                    if (position == 0) {    //判断当切换到第0个页面时把currentPosition设置为images.length,即倒数第二个位置，小圆点位置为length-1
                        currentPosition = banners.size();
                        dotPosition = banners.size() - 1;
                    } else if (position == banners.size() + 1) {    //当切换到最后一个页面时currentPosition设置为第一个位置，小圆点位置为0
                        currentPosition = 1;
                        dotPosition = 0;
                    } else {
                        currentPosition = position;
                        dotPosition = position - 1;
                    }
                    //  把之前的小圆点设置背景为暗红，当前小圆点设置为红色
                    mImageViewDotList.get(prePosition).setBackgroundResource(R.drawable.red_dot_night);
                    mImageViewDotList.get(dotPosition).setBackgroundResource(R.drawable.red_dot);
                    prePosition = dotPosition;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //当state为SCROLL_STATE_IDLE即没有滑动的状态时切换页面
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mViewPager.setCurrentItem(currentPosition, false);
                }
            }
        });

        //autoPlay();
    }

    private boolean isAutoPlay = false;

    /**
     * 设置自动播放
     */
    private void autoPlay() {
        //  确保执行一次(防止下拉刷新重复启动autoPlay)
        if (isAutoPlay) {
            return;
        }
        isAutoPlay = true;

        new Thread() {
            @Override
            public void run() {
                while (true) {
                    SystemClock.sleep(3000);
                    currentPosition++;
                    handler.sendEmptyMessage(1);
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(401);
        OkHttpUtils.getInstance().cancelTag(402);
    }
}
