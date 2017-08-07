package com.kingbox.ui.activity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kingbox.R;
import com.kingbox.utils.Config;
import com.kingbox.utils.PreferencesUtils;
import com.kingbox.utils.TagUtils;
import com.kingbox.utils.ToastUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

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

    private WebView mWebView;
    private FrameLayout mVideoContainer;
    private WebChromeClient.CustomViewCallback mCallBack;

    private int seconds = 0;
    private Timer timer;
    private TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_web_view);

        mWebView = (WebView) findViewById(R.id.webView);
        mVideoContainer = (FrameLayout) findViewById(R.id.videoContainer);

        initWebView();
        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
        mWebView.addJavascriptInterface(new JsObject(), "onClick");

    }

    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebChromeClient(new CustomWebViewChromeClient());
        mWebView.setWebViewClient(new CustomWebClient());

        mWebView.addJavascriptInterface(new JsObject(), "onClick");

        //mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        //mWebView.getSettings().setJavaScriptEnabled(true);
        //mWebView.getSettings().setUseWideViewPort(true); // 关键点
        // mWebView.getSettings().setAllowFileAccess(true); // 允许访问文件
        //mWebView.getSettings().setSupportZoom(true); // 支持缩放
        //  mWebView.getSettings().setLoadWithOverviewMode(true);
        //mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE); // 不加载缓存内容

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }*/
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

    private class JsObject {

        @JavascriptInterface
        public void fullscreen() {
            //监听到用户点击全屏按钮
            fullScreen();
        }
    }

    private class CustomWebViewChromeClient extends WebChromeClient {

        @Override
        public View getVideoLoadingProgressView() {
            FrameLayout frameLayout = new FrameLayout(VideoWebViewActivity.this);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            return frameLayout;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            fullScreen();
            bannerTopLayout.setVisibility(View.GONE);
            //bottomLayout.setVisibility(View.GONE);
            mWebView.setVisibility(View.GONE);
            mVideoContainer.setVisibility(View.VISIBLE);
            mVideoContainer.addView(view);
            mCallBack = callback;
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            fullScreen();
            if (mCallBack != null) {
                mCallBack.onCustomViewHidden();
            }
            bannerTopLayout.setVisibility(View.VISIBLE);
            //bottomLayout.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.VISIBLE);
            mVideoContainer.removeAllViews();
            mVideoContainer.setVisibility(View.GONE);
            super.onHideCustomView();
        }

        // 获取标题
        @Override
        public void onReceivedTitle(WebView view, String titles) {
            super.onReceivedTitle(view, titles);
            titleTV.setText(titles);
        }
    }

    private void fullScreen() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private class CustomWebClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            String js = TagUtils.getJs(url);
            view.loadUrl(js);
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
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    private void stopTimer(){
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
        mWebView.destroy();
    }

    @OnClick({R.id.play_img, R.id.back_img, R.id.refresh_img, R.id.favourite_img, R.id.home_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_img:   // 播放
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
                break;
            case R.id.home_img:   // 主页
                Config.isBackHome = true;
                finish();
                break;
        }
    }
}
