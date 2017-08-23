package com.kingbox.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.jsbridge.BridgeWebView;
import com.kingbox.jsbridge.DefaultHandler;
import com.kingbox.service.entity.UserInfo;
import com.kingbox.utils.Config;
import com.kingbox.utils.PreferencesUtils;
import com.kingbox.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


@SuppressLint("HandlerLeak")
public class BannerActivity extends BaseActivity {

    /**
     * 标题
     */
    @BindView(R.id.center_title_tv)
    TextView titleTV = null;

    @BindView(R.id.user_img)
    ImageView userImg;

    @BindView(R.id.play_img)
    ImageView playImg;

    @BindView(R.id.webview)
    BridgeWebView webView = null; // 网页显示的web

    @BindView(R.id.banner_top_layout)
    RelativeLayout bannerTopLayout = null;

    @BindView(R.id.bottom_layout)
    RelativeLayout bottomLayout = null;

    private String advLink = "";
    private String advPic = "";
    private String title = "";
    private String lastTitle = "";  // 记录上一次标题，用于返回处理标题显示(内嵌网页的情况)


    @BindView(R.id.video_fullView)
    FrameLayout videoFullView = null;

    private View xCustomView;
    private CustomViewCallback xCustomViewCallback;
    private myWebChromeClient xwebchromeclient;

    private boolean isJSLogin = false;     // 是否js登录

    private String type = "";

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2222:// 设置标题
                    String titles = (String) msg.obj;
                    if (!TextUtils.isEmpty(titles)) {
                        if (1 < type.length()) {
                            titleTV.setText(type);
                        } else {
                            titleTV.setText(titles);
                        }
                    }
                    break;
            }
        }
    };

    private int seconds = 0;
    private Timer timer = new Timer();
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {

            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    seconds++;
                    if (seconds >= Config.TIMES) {  // 三分钟
                        ToastUtils.ToastMessage(BannerActivity.this, "试看已结束,请续费观看");
                        finish();
                        return;
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banner_layout);

        viewInit();
    }

    @SuppressLint("NewApi")
    private void viewInit() {
        Intent intent = getIntent();
        advLink = intent.getStringExtra("url");

        type = intent.getStringExtra("type");
        if ("2".equals(type)) {
            playImg.setVisibility(View.VISIBLE);
            bottomLayout.setVisibility(View.VISIBLE);
        }

        webViewInit(advLink);

        if (0 == PreferencesUtils.getInt(BannerActivity.this, "isRecharge", -1) && 1 < type.length()) {
            int tempSeconds = PreferencesUtils.getInt(BannerActivity.this, Config.recordTime, 0);
            if (tempSeconds >= Config.TIMES) {  // 三分钟
                ToastUtils.ToastMessage(BannerActivity.this, "试看已结束,请续费观看");
                finish();
                return;
            }

            if (null != timer && null != task) {
                timer.schedule(task, 1000, 1000);       // timeTask
            }
            seconds += tempSeconds;
        }
    }

    @OnClick({R.id.play_img, R.id.back_img, R.id.refresh_img, R.id.favourite_img, R.id.home_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_img:   // 播放
                String url = "http://list.donewe.com/kkflv/index.php?url=" + webView.getUrl();
                webView.loadUrl(url);
                playImg.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                //webView.loadUrl("http://list.donewe.com/kkflv/index.php?url=http://www.iqiyi.com/v_19rr7tg8js.html?fc=87bbded392d221f5");
                //webView.loadUrl("http://list.donewe.com/kkflv/index.php?url=http://www.iqiyi.com/v_19rr75dxvw.html?fc=87bbded392d221f5");
                break;
            case R.id.back_img:  // 返回
                if (inCustomView()) {
                    hideCustomView();
                }else if (webView.canGoBack()/* && !isExit*/) {
                    if (playImg.getVisibility() == View.GONE) {
                        playImg.setVisibility(View.VISIBLE);
                        bottomLayout.setVisibility(View.VISIBLE);
                    }
                    webView.goBack();

                    // 处理返回时标题显示问题(内嵌网页的情况)
                    title = lastTitle;
                    if (1 < type.length()) {
                        titleTV.setText(type);
                    } else {
                        titleTV.setText(title);
                    }
                } else {
                    finish();
                }
                break;
            case R.id.refresh_img:   // 刷新
                webView.loadUrl(webView.getUrl());
                break;
            case R.id.favourite_img:    // 收藏
                break;
            case R.id.home_img:   // 主页
                Config.isBackHome = true;
                finish();
                break;
        }
    }

    private void webViewInit(String advLink) {
        if (!TextUtils.isEmpty(advLink)) {

            //setUrl(advLink);

            // WebView cookies清理
            CookieSyncManager.createInstance(this);
            CookieSyncManager.getInstance().startSync();
            CookieManager.getInstance().removeSessionCookie();

            // 清理cache 和历史记录
            webView.clearCache(true);
            webView.clearHistory();

            WebSettings webSettings = webView.getSettings();
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
            webSettings.setJavaScriptEnabled(true);
            // 设置可以访问文件
            webSettings.setAllowFileAccess(true);
            // 设置默认缩放方式尺寸是far
            webSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
            // 支持缩放
            webSettings.setSupportZoom(true);
            // 缩放按钮
            webSettings.setBuiltInZoomControls(true);
            webSettings.setUseWideViewPort(true);// 这个很关键
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setPluginState(WebSettings.PluginState.ON);
            webSettings.setMediaPlaybackRequiresUserGesture(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {   // 设置当一个安全站点企图加载来自一个不安全站点资源时WebView的行为(处理视频无法播放问题)
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }

			/*webInterface = new RSCWebInterface(BannerActivity.this);
            webView.addJavascriptInterface(webInterface, "RSCWebInterface");*/

            webView.setMainHandler(handler);   // 用于回传网页标题

            webView.setInitialScale(-1);// 为25%，最小缩放等级
            xwebchromeclient = new myWebChromeClient();
            webView.setWebChromeClient(xwebchromeclient);
            webView.setDownloadListener(new DownloadListener() {

                @Override
                public void onDownloadStart(String url, String userAgent,
                                            String contentDisposition, String mimetype,
                                            long contentLength) {
                    // 监听下载功能，当用户点击下载链接的时候，直接调用系统的浏览器来下载
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

            webView.setDefaultHandler(new DefaultHandler());

            getUserAgentType();

        }
    }
    private void getUserAgentType() {
        String mobile = PreferencesUtils.getString(BannerActivity.this, "mobile");
        String token = PreferencesUtils.getString(BannerActivity.this, "token");
        OkHttpUtils.get().url("http://admin.haizisou.cn/api/getUserAgentType?mobile=" + mobile + "&token=" + token).id(1701)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Config.isLogin = false;
                ToastUtils.ToastMessage(BannerActivity.this, "网络异常");
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
                        PreferencesUtils.putInt(BannerActivity.this, "type", userInfo.getType());
                        PreferencesUtils.putString(BannerActivity.this, "wechat", userInfo.getWechat());
                        webView.loadUrl(BannerActivity.this.advLink);
                    } else {
                        if (userInfo.getMsg().contains("token失效")) {
                            ToastUtils.ToastMessage(BannerActivity.this, "登录失效,请重新登录");
                            startActivity(new Intent(BannerActivity.this, LoginActivity.class));
                        } else {
                            ToastUtils.ToastMessage(BannerActivity.this, "接口出错");
                        }
                    }
                } catch (JSONException e) {
                }
            }
        });
    }

    public void pickFile() {
        Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooserIntent.setType("image/*");
        startActivityForResult(chooserIntent, RESULT_CODE);
    }

    public class myWebChromeClient extends WebChromeClient {

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
            this.openFileChooser(uploadMsg);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
            this.openFileChooser(uploadMsg);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            pickFile();
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public View getVideoLoadingProgressView() {
            FrameLayout frameLayout = new FrameLayout(BannerActivity.this);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            return frameLayout;
        }

        // 播放网络视频时全屏会被调用的方法
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            webView.setVisibility(View.INVISIBLE);
            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (xCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            bannerTopLayout.setVisibility(View.GONE);
            videoFullView.addView(view);
            xCustomView = view;
            xCustomViewCallback = callback;
            videoFullView.setVisibility(View.VISIBLE);
        }

        // 视频播放退出全屏会被调用的
        @Override
        public void onHideCustomView() {
            if (xCustomView == null)// 不是全屏播放状态
                return;
            bannerTopLayout.setVisibility(View.VISIBLE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            xCustomView.setVisibility(View.GONE);
            videoFullView.removeView(xCustomView);
            xCustomView = null;
            videoFullView.setVisibility(View.GONE);
            xCustomViewCallback.onCustomViewHidden();
            webView.setVisibility(View.VISIBLE);
        }

        // 获取标题
        @Override
        public void onReceivedTitle(WebView view, String titles) {
            super.onReceivedTitle(view, titles);
            if (1 < type.length()) {
                titleTV.setText(type);
            } else {
                titleTV.setText(titles);
            }

            // 获取网页标题
            title = titles;
            lastTitle = title;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            if (consoleMessage.message().contains("Uncaught ReferenceError")) {

            }
            return super.onConsoleMessage(consoleMessage);
        }
    }

    private void deleteCacheFolder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                clearCacheFolder(BannerActivity.this, getCacheDir(),
                        System.currentTimeMillis());
            }
        }).start();
    }

    /**
     * 清除内存中缓存数据
     *
     * @param context
     * @param dir
     * @param numDays
     * @return
     */
    public int clearCacheFolder(Context context, File dir, long numDays) {
        context.deleteDatabase("webview.db");
        context.deleteDatabase("webviewCache.db");
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(context, child,
                                numDays);
                    }
                    if (child.lastModified() < numDays) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.setVisibility(View.GONE);
            webView.destroy();
        }
        deleteCacheFolder();
        videoFullView.removeAllViews();

        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != task) {
            task.cancel();
            task = null;
        }
        if (0 != seconds) {
            PreferencesUtils.putInt(BannerActivity.this, Config.recordTime, seconds);
        }
        super.onDestroy();
    }

    @Override
    public void finish() {

        // 处理webView放大缩小的广播崩溃问题
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        if (null != view) {
            view.removeAllViews();
        }
        super.finish();
    }

    @Override
    protected void onResume() {
        try {
            webView.getClass().getMethod("onResume").invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();

        /**
         * 设置为横屏
         */
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onPause() {
        try {
            webView.getClass().getMethod("onPause").invoke(webView, (Object[]) null);
        }  catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    /**
     * 判断是否是全屏
     *
     * @return
     */
    public boolean inCustomView() {
        return (xCustomView != null);
    }

    /**
     * 全屏时按返加键执行退出全屏方法
     */
    public void hideCustomView() {
        xwebchromeclient.onHideCustomView();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /* 返回键的捕捉 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // 重复加载网页的直接finish(目前只有像网页需要登录且有视频播放的)
            if (isJSLogin) {
                finish();
                return false;
            }

            if (inCustomView()) {
                hideCustomView();
                return true;
            } else if (webView.canGoBack()) {
                if (playImg.getVisibility() == View.GONE) {
                    playImg.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.VISIBLE);
                }
                webView.goBack();
                return true;
            } else {
                finish();
                return false;
            }
        }
        return false; // false 是不管
    }

    private int RESULT_CODE = 0;

    private ValueCallback<Uri> mUploadMessage;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RESULT_CODE) {
            if (null == mUploadMessage) {
                return;
            }
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

	/*public class RSCWebInterface {

		Context mContext = null;

		*//** Instantiate the interface and set the context *//*
        public RSCWebInterface(Context c) {
			mContext = c;
		}

		*//** Show a toast from the web page *//*
        // 如果target 大于等于API 17，则需要加上如下注解
		@JavascriptInterface
		public void nativeShare1(String data) {

			JSONObject jsonObject = JSONObject.parseObject(data);
			if (!jsonObject.isEmpty()) {
				String shareImg = "";
				String shareLink = "";
				String desc = "";
				if (jsonObject.containsKey("imgUrl")) {
					shareImg = jsonObject.getString("imgUrl");
					if (StringUtils.isEmpty(shareImg)) shareImg = "";
				}
				if (jsonObject.containsKey("link")) {
					shareLink = jsonObject.getString("link");
					if (StringUtils.isEmpty(shareLink)) shareLink = "";
				}
				if (jsonObject.containsKey("desc")) {
					desc = jsonObject.getString("desc");
					if (StringUtils.isEmpty(desc)) desc = "";
				}
				if (null == meet) {
					meet = new Meet();
				}
				meet.setShareTitle(title);
				meet.setShareImgUrl(shareImg);
				meet.setShareLink(shareLink);
				meet.setShareDesc(desc);
				meet.setMid("");

				shareFlag = false;
			} else {
				UIUtils.ToastMessage(mContext, "网络繁忙,获取分享内容失败!");
			}
		}
	}*/
}
