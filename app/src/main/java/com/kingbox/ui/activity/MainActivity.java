package com.kingbox.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.service.entity.UpdateVersion;
import com.kingbox.ui.fragment.LiveSquareFragment1;
import com.kingbox.ui.fragment.OnlineCinemaFragment;
import com.kingbox.ui.fragment.StarTVLiveFragment;
import com.kingbox.ui.fragment.WelfareVideoFragment;
import com.kingbox.utils.Config;
import com.kingbox.utils.PreferencesUtils;
import com.kingbox.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;

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

    private ProgressDialog progressDialog;

    private AlertDialog updateAlertDialog;

    // 外存sdcard存放路径
    //private static final String FILE_PATH = Environment.getExternalStorageDirectory() + "/";
    // 下载应用存放全路径
    //private static final String FILE_NAME = FILE_PATH + "kingbox.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(savedInstanceState!=null){ FragmentManager manager = getSupportFragmentManager(); manager.popBackStackImmediate(null, 1); }
        Config.isExitMain = true;

        initView();
        if (null == savedInstanceState) {
            initData();
        }
        getUpdateVersion();
    }

    private void getUpdateVersion() {
        PackageManager manager = getPackageManager();
        String version = "";
        PackageInfo info;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
            version = info.versionCode + "";
        } catch (PackageManager.NameNotFoundException e) {
            version = "";
        }
        OkHttpUtils.get().url("http://admin.haizisou.cn/api/autoUpdateAPP?type=Android&version=" + version).id(10001)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Gson gson = new Gson();
                    UpdateVersion updateVersion = gson.fromJson(jsonObject.toString(), UpdateVersion.class);
                    if (updateVersion.getUpdate().equals("1")) {
                        showNoticeDialog(updateVersion);
                    }
                } catch (JSONException e) {
                }

            }
        });
    }

    /**
     * 显示提示更新对话框
     */
    private void showNoticeDialog(final UpdateVersion updateVersion) {
        updateAlertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("检测到新版本!")
                .setMessage("V" + updateVersion.getVersion())
                .setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        downloadInstallDialog(updateVersion);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).create();
        updateAlertDialog.setCancelable(false);
        updateAlertDialog.show();
    }

    private void downloadInstallDialog(UpdateVersion updateVersion) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("正在下载...");
        progressDialog.setCanceledOnTouchOutside(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        OkHttpUtils.get().url(updateVersion.getUrl()/*"http://download.sj.qq.com/upload/connAssitantDownload/upload/MobileAssistant_1.apk"*/).id(10000)   // 请求Id
                .build().execute(new FileCallBack(Environment.getExternalStorageDirectory().getAbsolutePath(), "kingbox.apk") {

            @Override
            public void onBefore(Request request, int id)
            {
                progressDialog.show();
            }

            @Override
            public void inProgress(float progress, long total, int id)
            {
                progressDialog.setProgress((int) (100 * progress));
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                progressDialog.dismiss();//关闭进度条
                ToastUtils.ToastMessage(MainActivity.this, "下载出错,请联系管理员");
                finish();
            }

            @Override
            public void onResponse(File response, int id) {
                progressDialog.dismiss();//关闭进度条
                installApp();
            }
        });
    }

    /**
     * 安装新版本应用
     */
    private void installApp() {
        File appFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/kingbox.apk");
        if (!appFile.exists()) {
            return;
        }
        // 跳转到新版本应用安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + appFile.toString()), "application/vnd.android.package-archive");
        startActivity(intent);
        finish();
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
        //vpTitleLayout.setVisibility(View.VISIBLE);
        centerTitleTv.setText("在线影院");
        centerTitleTv.setVisibility(View.VISIBLE);
        addFragment();
        currentFragment = onlineCinemaFragment;
    }

    private void addFragment() {
        if (!onlineCinemaFragment.isAdded()) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.fragment_frame_layout, onlineCinemaFragment, "");
            transaction.commitAllowingStateLoss();
        }
    }

    private void switchFragment(Fragment from, Fragment to) {
        if (currentFragment != to) {
            currentFragment = to;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(from).add(R.id.fragment_frame_layout, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
        }
    }

    @OnClick({R.id.user_img, R.id.live_square_img, R.id.welfare_video_img, R.id.star_tv_live_img, R.id.online_cinema_img, R.id.aaa_tv, R.id.bbb_tv, R.id.ccc_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_img:   // 用户头像
                String mobile = PreferencesUtils.getString(MainActivity.this, "mobile", "");
                if (TextUtils.isEmpty(mobile))
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                else
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
            if (null != updateAlertDialog || null != progressDialog) {
                return true;
            }
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
