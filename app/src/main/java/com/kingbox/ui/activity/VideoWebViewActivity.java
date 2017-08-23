package com.kingbox.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.service.entity.UserInfo;
import com.kingbox.utils.Config;
import com.kingbox.utils.PreferencesUtils;
import com.kingbox.utils.ToastUtils;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2017/7/26.
 */

public class VideoWebViewActivity extends BaseActivity {

    @BindView(R.id.back_img)
    ImageView backImg;

    /**
     * 标题
     */
    @BindView(R.id.center_title_tv)
    TextView titleTV = null;

    @BindView(R.id.user_img)
    ImageView userImg;

    @BindView(R.id.play_img)
    ImageView playImg;

    @BindView(R.id.banner_top_layout)
    RelativeLayout bannerTopLayout = null;

    @BindView(R.id.bottom_layout)
    RelativeLayout bottomLayout = null;

    private com.tencent.smtt.sdk.WebView mWebView;

    private int seconds = 0;
    private Timer timer;
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //preinitX5WebCore();
        setContentView(R.layout.activity_video_web_view);

        mWebView = (com.tencent.smtt.sdk.WebView) findViewById(R.id.webView);
        //getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //mWebView.setLayerType();
        mWebView.setDrawingCacheEnabled(true);

        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);

        mWebView.setWebChromeClient(new myWebChromeClient());
        mWebView.setWebViewClient(new myWebViewClient());
        com.tencent.smtt.sdk.WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new com.tencent.smtt.sdk.WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(com.tencent.smtt.sdk.WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        if (0 == getIntent().getIntExtra("histroy", 1)) {
            playImg.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
        } else {
            playImg.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        switch (config.orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            if (View.GONE == playImg.getVisibility()) {
                playImg.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
                stopTimer();
            }
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
        mWebView.resumeTimers();
        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        mWebView.pauseTimers();
    }

    private void stopTimer() {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != task) {
            task.cancel();
            task = null;
        }
        if (0 != seconds) {
            PreferencesUtils.putInt(VideoWebViewActivity.this, Config.recordTime, seconds);
        }
    }

    @Override
    public void onDestroy() {
        stopTimer();

        super.onDestroy();
        mWebView.loadUrl("about:blank");
        mWebView.stopLoading();
        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        mWebView.destroy();
        mWebView = null;
    }

    @OnClick({R.id.play_img, R.id.back_img, R.id.refresh_img, R.id.favourite_img, R.id.home_img,R.id.user_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_img:   // 播放
                getUserAgentType();
                break;
            case R.id.back_img:  // 返回
                if (mWebView.canGoBack()) {
                    if (playImg.getVisibility() == View.GONE) {
                        playImg.setVisibility(View.VISIBLE);
                        bottomLayout.setVisibility(View.VISIBLE);
                    }
                    mWebView.goBack();
                } else {
                    finish();
                }
                break;
            case R.id.refresh_img:   // 刷新
                mWebView.loadUrl(mWebView.getUrl());
                break;
            case R.id.favourite_img:    // 收藏
                ToastUtils.ToastMessage(VideoWebViewActivity.this, "设置书签成功");

                String Bookmark;
                String temps = PreferencesUtils.getString(VideoWebViewActivity.this, "Bookmark", "");
                if (!TextUtils.isEmpty(temps)) {
                    Bookmark = title + "##" + mWebView.getUrl() + "@#" + temps;
                } else {
                    Bookmark = title + "##" + mWebView.getUrl();
                }
                PreferencesUtils.putString(VideoWebViewActivity.this, "Bookmark", Bookmark);
                break;
            case R.id.home_img:   // 主页
                Config.isBackHome = true;
                finish();
                break;
            case R.id.user_img:  // 用户头像
                String mobile = PreferencesUtils.getString(VideoWebViewActivity.this, "mobile", "");
                if (TextUtils.isEmpty(mobile))
                    startActivity(new Intent(VideoWebViewActivity.this, LoginActivity.class));
                else
                    startActivity(new Intent(VideoWebViewActivity.this, UserCenterActivity.class));
                break;
        }
    }

    private void play() {
        if (0 == PreferencesUtils.getInt(VideoWebViewActivity.this, "isRecharge", -1)) {
            int tempSeconds = PreferencesUtils.getInt(VideoWebViewActivity.this, Config.recordTime, 0);
            if (tempSeconds >= Config.TIMES) {  // 三分钟
                ToastUtils.ToastMessage(VideoWebViewActivity.this, "试看已结束,请续费观看");
                return;
            }
            seconds = 0;
            timer = new Timer();
            task = new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {      // UI thread
                        @Override
                        public void run() {
                            seconds++;
                            if (seconds >= Config.TIMES) {  // 三分钟
                                ToastUtils.ToastMessage(VideoWebViewActivity.this, "试看已结束,请续费观看");
                                stopTimer();
                                backImg.performClick();
                                return;
                            }
                            Log.i("VideoPlayerActivity", seconds + "====");
                        }
                    });
                }
            };
            if (null != timer && null != task) {
                timer.schedule(task, 1000, 1000);       // timeTask
            }
            seconds += tempSeconds;
        }

        String baseUrl = mWebView.getUrl();
        mWebView.loadUrl("http://list.donewe.com/kkflv/index.php?url=" + baseUrl);
        playImg.setVisibility(View.GONE);
        bottomLayout.setVisibility(View.GONE);

        String histroy;
        String temp = PreferencesUtils.getString(VideoWebViewActivity.this, "histroy", "");
        if (!TextUtils.isEmpty(temp)) {
            histroy = title + "##" + mWebView.getUrl() + "@#" + temp;
        } else {
            histroy = title + "##" + mWebView.getUrl();
        }
        PreferencesUtils.putString(VideoWebViewActivity.this, "histroy", histroy);
    }

    private String title = "";

    public class myWebChromeClient extends WebChromeClient {

        // 获取标题
        @Override
        public void onReceivedTitle(WebView view, String titles) {
            super.onReceivedTitle(view, titles);

            // 获取网页标题
            if (!titles.equals("KINGBOX")) {
                titleTV.setText(titles);
                title = titles;
            }
        }
    }

    public class myWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView webView, String s) {
            super.onPageFinished(webView, s);
            if (View.GONE == playImg.getVisibility()) {
                String histroy = PreferencesUtils.getString(VideoWebViewActivity.this, "histroy", "");
                histroy = title + "##" + webView.getUrl() + "@#" + histroy;
                PreferencesUtils.putString(VideoWebViewActivity.this, "histroy", histroy);
            }
        }
    }

    private void getUserAgentType() {
        String mobile = PreferencesUtils.getString(VideoWebViewActivity.this, "mobile");
        String token = PreferencesUtils.getString(VideoWebViewActivity.this, "token");
        OkHttpUtils.get().url("http://admin.haizisou.cn/api/getUserAgentType?mobile=" + mobile + "&token=" + token).id(1601)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Config.isLogin = false;
                ToastUtils.ToastMessage(VideoWebViewActivity.this, "网络异常");
                finish();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Gson gson = new Gson();
                    UserInfo userInfo = gson.fromJson(jsonObject.toString(), UserInfo.class);
                    if (userInfo.isSuccess() && 0 == userInfo.getCode()) {  // 成功
                        Config.isLogin = true;
                        PreferencesUtils.putInt(VideoWebViewActivity.this, "type", userInfo.getType());
                        PreferencesUtils.putString(VideoWebViewActivity.this, "wechat", userInfo.getWechat());
                        play();
                    } else {
                        if (userInfo.getMsg().contains("token失效")) {
                            ToastUtils.ToastMessage(VideoWebViewActivity.this, "登录失效,请重新登录");
                            startActivity(new Intent(VideoWebViewActivity.this, LoginActivity.class));
                            //VideoWebViewActivity.this.finish();
                        } else {
                            ToastUtils.ToastMessage(VideoWebViewActivity.this, "接口出错");
                        }
                    }

                } catch (JSONException e) {
                }
            }
        });
    }
}
