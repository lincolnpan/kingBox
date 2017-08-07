package com.kingbox.holder;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingbox.R;
import com.kingbox.adapter.BannerPagerAdapter;
import com.kingbox.adapter.MultiTypeAdapter;
import com.kingbox.service.entity.Banner;
import com.kingbox.service.entity.Title;
import com.kingbox.ui.activity.BannerActivity;
import com.kingbox.ui.activity.NoticesActivity;
import com.kingbox.utils.GlideCatchUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/7/12.
 */
public class TitleViewHolder extends BaseViewHolder<Title> {

    @BindView(R.id.vp_main)
    ViewPager mViewPager;

    @BindView(R.id.ll_main_dot)
    LinearLayout llMainDot;


    @BindView(R.id.boradcast_tv)
    TextView boradcast_tv;

    @BindView(R.id.title_tv)
    TextView namTv;

    private Context context;

    private List<ImageView> mImageViewList;
    private int currentPosition = 0;
    private int dotPosition = 0;
    private int prePosition = 0;
    private List<ImageView> mImageViewDotList;

    private List<Banner> bannerList = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:    // 自动轮播
                    mViewPager.setCurrentItem(currentPosition, false);
                    break;
            }
        }
    };

    public TitleViewHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        itemView.setLayoutParams(params);
    }

    @Override
    public void setUpView(Title model, int position, MultiTypeAdapter adapter) {
        namTv.setText(model.getName());
        bannerList = model.getBannerList();
        initData();   // 初始化数据
        setDot();    // 设置位置圆点
        setViewPager();

        if (1 == model.getType()) {
            boradcast_tv.setVisibility(View.VISIBLE);
        } else {
            boradcast_tv.setVisibility(View.GONE);
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
        for (int i = 0; i < bannerList.size(); i++) {
            ImageView imageViewDot = new ImageView(context);
            imageViewDot.setLayoutParams(params);
            //  设置小圆点的背景为灰色
            imageViewDot.setBackgroundResource(R.drawable.red_dot_night);
            llMainDot.addView(imageViewDot);
            mImageViewDotList.add(imageViewDot);
        }
        //设置第一个小圆点图片背景为白色
        mImageViewDotList.get(dotPosition).setBackgroundResource(R.drawable.red_dot);
    }

    private void initData() {
        mImageViewList = new ArrayList<>();
        ImageView imageView;
        for (int i = 0; i < bannerList.size() + 2; i++) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            if (i == 0) {   //判断当i=0为该处的ImageView设置最后一张图片作为背景
                GlideCatchUtil.getInstance().ImageLoading(context, bannerList.get(bannerList.size() - 1).getPic(), imageView, R.drawable.banner_df_icon);
                imageView.setOnClickListener(new ImageViewOnclickListener(bannerList.size() - 1));   //  设置点击事件
                mImageViewList.add(imageView);
            } else if (i == bannerList.size() + 1) {   //判断当i=images.length+1时为该处的ImageView设置第一张图片作为背景
                GlideCatchUtil.getInstance().ImageLoading(context, bannerList.get(0).getPic(), imageView, R.drawable.banner_df_icon);
                imageView.setOnClickListener(new ImageViewOnclickListener(0));   //  设置点击事件
                mImageViewList.add(imageView);
            } else {  //其他情况则为ImageView设置images[i-1]的图片作为背景
                GlideCatchUtil.getInstance().ImageLoading(context, bannerList.get(i - 1).getPic(), imageView, R.drawable.banner_df_icon);
                imageView.setOnClickListener(new ImageViewOnclickListener(i - 1));   //  设置点击事件
                mImageViewList.add(imageView);
            }
        }
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
            Banner banner = bannerList.get(position);
            Intent intent = new Intent(context, BannerActivity.class);
            intent.putExtra("url", banner.getUrl());
            intent.putExtra("type", "1");
            context.startActivity(intent);
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
                if (null != mImageViewDotList && mImageViewDotList.size() > 0 && null != bannerList && bannerList.size() > 0) {
                    if (position == 0) {    //判断当切换到第0个页面时把currentPosition设置为images.length,即倒数第二个位置，小圆点位置为length-1
                        currentPosition = bannerList.size();
                        dotPosition = bannerList.size() - 1;
                    } else if (position == bannerList.size() + 1) {    //当切换到最后一个页面时currentPosition设置为第一个位置，小圆点位置为0
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

    @OnClick(R.id.boradcast_tv)
    public void notice() {
        Intent intent = new Intent(context, NoticesActivity.class);
        intent.putExtra("type", 1);
        context.startActivity(intent);
    }
}
