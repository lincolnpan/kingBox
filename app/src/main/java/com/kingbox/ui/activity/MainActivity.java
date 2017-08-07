package com.kingbox.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingbox.R;
import com.kingbox.ui.fragment.LiveSquareFragment1;
import com.kingbox.ui.fragment.OnlineCinemaFragment;
import com.kingbox.ui.fragment.StarTVLiveFragment;
import com.kingbox.ui.fragment.WelfareVideoFragment;
import com.kingbox.utils.Config;
import com.kingbox.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity {

    @BindView(R.id.back_img)
    ImageView backImg;

    @BindView(R.id.center_title_tv)
    TextView centerTitleTv;

    @BindView(R.id.user_img)
    ImageView userImg;

    @BindView(R.id.live_square_img)
    ImageView liveSquareImg;

    @BindView(R.id.welfare_video_img)
    ImageView welfareVideoImg;

    @BindView(R.id.star_tv_live_img)
    ImageView starTvLiveImg;

    @BindView(R.id.online_cinema_img)
    ImageView onlineCinemaImg;

    @BindView(R.id.vp_title_layout)
    LinearLayout vpTitleLayout;

    @BindView(R.id.aaa_tv)
    TextView aaaTv;

    @BindView(R.id.bbb_tv)
    TextView bbbTv;

    @BindView(R.id.ccc_tv)
    TextView cccTv;
    /**
     * 直播广场fragment
     */
    private LiveSquareFragment1 liveSquareFragment;

    /**
     * 福利视频fragment
     */
    private WelfareVideoFragment welfareVideoFragment;

    /**
     * 卫视直播fragment
     */
    private StarTVLiveFragment starTVLiveFragment;

    /**
     * 在线影院
     */
    private OnlineCinemaFragment onlineCinemaFragment;

    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Config.isExitMain = true;

        initView();
        initData();
    }

    private void initView() {
        backImg.setVisibility(View.GONE);
        centerTitleTv.setText("帝王宝盒");
    }

    private void initData() {
        if (null == liveSquareFragment) {
            liveSquareFragment = new LiveSquareFragment1();
        }
        if (null == welfareVideoFragment) {
            welfareVideoFragment = new WelfareVideoFragment();
        }
        if (null == starTVLiveFragment) {
            starTVLiveFragment = new StarTVLiveFragment();
        }
        if (null == onlineCinemaFragment) {
            onlineCinemaFragment = new OnlineCinemaFragment();
        }
        vpTitleLayout.setVisibility(View.VISIBLE);
        centerTitleTv.setVisibility(View.GONE);
        addFragment();
        currentFragment = liveSquareFragment;
    }

    private void addFragment() {
        if (!liveSquareFragment.isAdded()) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_frame_layout, liveSquareFragment, "");
            transaction.commitAllowingStateLoss();
        }
    }

    private void switchFragment(Fragment from, Fragment to) {
        if (currentFragment != to) {
            currentFragment = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fragment_frame_layout, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    @OnClick({R.id.user_img, R.id.live_square_img, R.id.welfare_video_img, R.id.star_tv_live_img, R.id.online_cinema_img, R.id.aaa_tv, R.id.bbb_tv, R.id.ccc_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_img:   // 用户头像
                startActivity(new Intent(MainActivity.this, UserCenterActivity.class));
                break;
            case R.id.live_square_img:    // 直播广场
                liveSquareImg.setImageResource(R.drawable.live_square_selected_icon);
                welfareVideoImg.setImageResource(R.drawable.welfare_video_icon);
                starTvLiveImg.setImageResource(R.drawable.star_tv_live_icon);
                onlineCinemaImg.setImageResource(R.drawable.online_cinema_icon);
                centerTitleTv.setText("帝王宝盒");
                switchFragment(currentFragment, liveSquareFragment);
                vpTitleLayout.setVisibility(View.VISIBLE);
                centerTitleTv.setVisibility(View.GONE);
                break;
            case R.id.welfare_video_img:  // 福利视频
                liveSquareImg.setImageResource(R.drawable.live_square_icon);
                welfareVideoImg.setImageResource(R.drawable.welfare_video_icon);
                starTvLiveImg.setImageResource(R.drawable.star_tv_live_icon);
                onlineCinemaImg.setImageResource(R.drawable.online_cinema_icon);
                centerTitleTv.setText("福利视频");
                switchFragment(currentFragment, welfareVideoFragment);
                vpTitleLayout.setVisibility(View.GONE);
                centerTitleTv.setVisibility(View.VISIBLE);
                break;
            case R.id.star_tv_live_img:    // 卫视直播
                liveSquareImg.setImageResource(R.drawable.live_square_icon);
                welfareVideoImg.setImageResource(R.drawable.welfare_video_icon);
                starTvLiveImg.setImageResource(R.drawable.star_tv_live_selected_icon);
                onlineCinemaImg.setImageResource(R.drawable.online_cinema_icon);
                centerTitleTv.setText("卫视直播");
                switchFragment(currentFragment, starTVLiveFragment);
                vpTitleLayout.setVisibility(View.GONE);
                centerTitleTv.setVisibility(View.VISIBLE);
                break;
            case R.id.online_cinema_img:     // 在线影院
                liveSquareImg.setImageResource(R.drawable.live_square_icon);
                welfareVideoImg.setImageResource(R.drawable.welfare_video_icon);
                starTvLiveImg.setImageResource(R.drawable.star_tv_live_icon);
                onlineCinemaImg.setImageResource(R.drawable.online_cinema_selected_icon);
                centerTitleTv.setText("在线影院");
                switchFragment(currentFragment, onlineCinemaFragment);
                vpTitleLayout.setVisibility(View.GONE);
                centerTitleTv.setVisibility(View.VISIBLE);
                break;
            case R.id.aaa_tv:
                liveSquareFragment.setVP(0);
                break;
            case R.id.bbb_tv:
                liveSquareFragment.setVP(1);
                break;
            case R.id.ccc_tv:
                liveSquareFragment.setVP(2);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Config.isBackHome) {   // 跳转主页
            Config.isBackHome = false;
            liveSquareImg.performClick();
        }
    }

    public void setVpTitle(int position) {
        if (0 == position) {
            aaaTv.setBackgroundResource(R.drawable.aaa);
            bbbTv.setBackground(null);
            cccTv.setBackground(null);
        } else if (1 == position) {
            bbbTv.setBackgroundResource(R.drawable.bbb);
            aaaTv.setBackground(null);
            cccTv.setBackground(null);
        } else if (2 == position) {
            cccTv.setBackgroundResource(R.drawable.ccc);
            bbbTv.setBackground(null);
            aaaTv.setBackground(null);
        }
    }

    private long exitTime = 0; // 记录退出的时间

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.ToastMessage(MainActivity.this, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Config.isExitMain = false;
    }
}
