package com.kingbox.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.listener.OnTransitionListener;
import com.kingbox.service.entity.UserInfo;
import com.kingbox.utils.Config;
import com.kingbox.utils.PreferencesUtils;
import com.kingbox.utils.ToastUtils;
import com.kingbox.view.StandardGSYVideoPlayer;
import com.shuyu.gsyvideoplayer.GSYVideoPlayer;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import okhttp3.Call;

/**
 * 播放器
 * Created by Administrator on 2017/7/12.
 */
public class VideoPlayerActivity extends BaseActivity {

    public final static String IMG_TRANSITION = "IMG_TRANSITION";

    public final static String TRANSITION = "TRANSITION";

    @BindView(R.id.back_img)
    ImageView backImg;

    @BindView(R.id.center_title_tv)
    TextView centerTitleTv;

    @BindView(R.id.user_img)
    ImageView userImg;

    @BindView(R.id.video_player)
    StandardGSYVideoPlayer videoPlayer;

    OrientationUtils orientationUtils;

    private boolean isTransition;

    private Transition transition;

    private String imgUrl;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1111:
                    if (0 == PreferencesUtils.getInt(VideoPlayerActivity.this, "isRecharge", -1)) {
                        startTimer();   // 开始计时
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
                        ToastUtils.ToastMessage(VideoPlayerActivity.this, "试看已结束,请续费观看");
                        finish();
                        return;
                    }
                    Log.i("VideoPlayerActivity", seconds + "====");
                }
            });
        }
    };

    private void startTimer() {
        if (null != timer && null != task) {
            timer.schedule(task, 1000, 1000);       // timeTask
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        if (0 == PreferencesUtils.getInt(VideoPlayerActivity.this, "isRecharge", -1)) {
            int tempSeconds = PreferencesUtils.getInt(VideoPlayerActivity.this, Config.recordTime, 0);
            if (tempSeconds >= Config.TIMES) {  // 三分钟
                ToastUtils.ToastMessage(VideoPlayerActivity.this, "试看已结束,请续费观看");
                finish();
                return;
            }
            seconds += tempSeconds;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        init();
    }

    private void init() {
        isTransition = getIntent().getBooleanExtra(TRANSITION, false);


        String url = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            ToastUtils.ToastMessage(VideoPlayerActivity.this, "播放地址错误");
            finish();
            return;
        }
        imgUrl = getIntent().getStringExtra("imgUrl");

        /*String source1 = url;//"http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
        String name = "普通";
        SwitchVideoModel switchVideoModel = new SwitchVideoModel(name, source1);

        String source2 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4";
        String name2 = "清晰";
        SwitchVideoModel switchVideoModel2 = new SwitchVideoModel(name2, source2);

        List<SwitchVideoModel> list = new ArrayList<>();
        list.add(switchVideoModel);
        list.add(switchVideoModel2);*/

        videoPlayer.setHandler(handler);
        videoPlayer.setUp(url, true, "");

        //增加封面
        /*ImageView imageView = new ImageView(VideoPlayerActivity.this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GlideCatchUtil.getInstance().ImageLoading(VideoPlayerActivity.this, imgUrl, imageView);
        videoPlayer.setThumbImageView(imageView);*/


        //增加title
        /*videoPlayer.getTitleTextView().setVisibility(View.VISIBLE);
        videoPlayer.getTitleTextView().setText("测试视频");*/

        //设置返回键
        videoPlayer.getBackButton().setVisibility(View.VISIBLE);

        //设置旋转
        orientationUtils = new OrientationUtils(this, videoPlayer);

        //设置全屏按键功能
        videoPlayer.getFullscreenButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orientationUtils.resolveByClick();
            }
        });

        //videoPlayer.setBottomProgressBarDrawable(getResources().getDrawable(R.drawable.video_new_progress));
        //videoPlayer.setDialogVolumeProgressBar(getResources().getDrawable(R.drawable.video_new_volume_progress_bg));
        //videoPlayer.setDialogProgressBar(getResources().getDrawable(R.drawable.video_new_progress));
        //videoPlayer.setBottomShowProgressBarDrawable(getResources().getDrawable(R.drawable.video_new_seekbar_progress),
        //getResources().getDrawable(R.drawable.video_new_seekbar_thumb));
        //videoPlayer.setDialogProgressColor(getResources().getColor(R.color.colorAccent), -11);

        //是否可以滑动调整
        videoPlayer.setIsTouchWiget(true);

        //设置返回按键功能
        videoPlayer.getBackButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //过渡动画
        initTransition();

        getUserAgentType();
    }

    private void getUserAgentType() {
        String mobile = PreferencesUtils.getString(VideoPlayerActivity.this, "mobile");
        String token = PreferencesUtils.getString(VideoPlayerActivity.this, "token");
        OkHttpUtils.get().url("http://admin.haizisou.cn/api/getUserAgentType?mobile=" + mobile + "&token=" + token).id(1501)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Config.isLogin = false;
                ToastUtils.ToastMessage(VideoPlayerActivity.this, "网络异常");
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
                        PreferencesUtils.putInt(VideoPlayerActivity.this, "type", userInfo.getType());
                        PreferencesUtils.putString(VideoPlayerActivity.this, "wechat", userInfo.getWechat());
                        videoPlayer.startPlayLogic();   // 开始播放
                    } else {
                        if (userInfo.getMsg().contains("token失效")) {
                            ToastUtils.ToastMessage(VideoPlayerActivity.this, "登录失效,请重新登录");
                            finish();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(VideoPlayerActivity.this, LoginActivity.class));
                                }
                            }, 300);
                        } else {
                            ToastUtils.ToastMessage(VideoPlayerActivity.this, "接口出错");
                        }
                    }
                } catch (JSONException e) {
                }
            }
        });
    }

    private void initTransition() {
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
            ViewCompat.setTransitionName(videoPlayer, IMG_TRANSITION);
            addTransitionListener();
            startPostponedEnterTransition();
        } else {
            videoPlayer.startPlayLogic();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean addTransitionListener() {
        transition = getWindow().getSharedElementEnterTransition();
        if (transition != null) {
            transition.addListener(new OnTransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    super.onTransitionEnd(transition);
                    videoPlayer.startPlayLogic();
                    transition.removeListener(this);
                }
            });
            return true;
        }
        return false;
    }

    /*@OnClick(R.id.back_img)
    public void onClick(){
        onBackPressed();
    }
*/
    @Override
    public void onBackPressed() {
        //先返回正常状态
        if (orientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            videoPlayer.getFullscreenButton().performClick();
            return;
        }
        //释放所有
        videoPlayer.setStandardVideoAllCallBack(null);
        GSYVideoPlayer.releaseAllVideos();
        if (isTransition && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.onBackPressed();
        } else {
            finish();
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
    }

    @Override
    protected void onDestroy() {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != task) {
            task.cancel();
            task = null;
        }
        if (0 != seconds) {
            PreferencesUtils.putInt(VideoPlayerActivity.this, Config.recordTime, seconds);
        }
        OkHttpUtils.getInstance().cancelTag(1501);
        super.onDestroy();
    }
}
