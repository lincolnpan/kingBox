package com.kingbox.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.service.entity.UserInfo;
import com.kingbox.utils.Config;
import com.kingbox.utils.PreferencesUtils;
import com.kingbox.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 用户中心
 * Created by Administrator on 2017/7/8.
 */
public class UserCenterActivity extends BaseActivity {

    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.center_title_tv)
    TextView centerTitleTv;
    @BindView(R.id.user_img)
    ImageView userImg;
    @BindView(R.id.current_user_img)
    ImageView currentUserImg;
    @BindView(R.id.name_tv)
    TextView nameTv;
    @BindView(R.id.id_tv)
    TextView idTv;
    @BindView(R.id.update_password_tv)
    TextView updatePasswordTv;
    @BindView(R.id.explain_tv)
    TextView explainTv;
    @BindView(R.id.super_member_img)
    ImageView superMemberImg;
    @BindView(R.id.super_member_end_time_tv)
    TextView superMemberEndTimeTv;
    @BindView(R.id.super_member_renew_tv)
    TextView superMemberRenewTv;
    @BindView(R.id.extreme_member_img)
    ImageView extremeMemberImg;
    @BindView(R.id.extreme_member_end_time_tv)
    TextView extremeMemberEndTimeTv;
    @BindView(R.id.extreme_member_renew_tv)
    TextView extremeMemberRenewTv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        /*UserInfo info = (UserInfo) getIntent().getSerializableExtra("userInfo");
        if (null == info) {
            login();
        }*/
        initView();
        refreshData();
    }

    private void refreshData() {
        String name = PreferencesUtils.getString(UserCenterActivity.this, "username");
        String id = PreferencesUtils.getString(UserCenterActivity.this, "userId");
        String expireTime = PreferencesUtils.getString(UserCenterActivity.this, "expireTime");
        try {
            expireTime = expireTime.substring(0, 10);
        } catch (Exception e) {
            expireTime = "";
        }
        nameTv.setText(name);
        idTv.setText("id: " + id);
        superMemberEndTimeTv.setText("到期时间：" + expireTime);
        extremeMemberEndTimeTv.setText("到期时间：" + expireTime);

        //int type = PreferencesUtils.getInt(UserCenterActivity.this, "type", -1);

        getUserAgentType();

    }

    private void getUserAgentType() {
        String mobile = PreferencesUtils.getString(UserCenterActivity.this, "mobile");
        String token = PreferencesUtils.getString(UserCenterActivity.this, "token");
        OkHttpUtils.get().url("http://admin.haizisou.cn/api/getUserAgentType?mobile=" + mobile + "&token=" + token).id(1401)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Config.isLogin = false;
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Gson gson = new Gson();
                    UserInfo userInfo = gson.fromJson(jsonObject.toString(), UserInfo.class);
                    if (userInfo.isSuccess() && 0 == userInfo.getCode()) {  // 成功
                        Config.isLogin = true;
                        PreferencesUtils.putInt(UserCenterActivity.this, "type", userInfo.getType());
                        PreferencesUtils.putString(UserCenterActivity.this, "wechat", userInfo.getWechat());
                    } else {
                        if (userInfo.getMsg().contains("token失效")) {
                            ToastUtils.ToastMessage(UserCenterActivity.this, "登录失效,请重新登录");
                            startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
                            //UserCenterActivity.this.finish();
                        } else {
                            ToastUtils.ToastMessage(UserCenterActivity.this, "接口出错");
                        }
                    }

                } catch (JSONException e) {
                }
            }
        });
    }

    private void initView() {
        centerTitleTv.setText("个人中心");
        userImg.setVisibility(View.GONE);
    }

    @OnClick({R.id.back_img, R.id.update_password_tv, R.id.explain_tv, R.id.exit_tv, R.id.super_member_renew_tv, R.id.extreme_member_renew_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_img:   // 返回
                toMain();
                break;
            case R.id.update_password_tv:    // 修改密码
                startActivity(new Intent(UserCenterActivity.this, UpdatePasswordActivity.class));
                break;
            case R.id.explain_tv:   // 盒子说明
                startActivity(new Intent(UserCenterActivity.this, BoxExplainActivity.class));
                break;
            case R.id.exit_tv:   // 退出登录
                Config.isLogin = false;
                Config.clearUserInfo(UserCenterActivity.this);
                startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
                finish();
                break;
            case R.id.super_member_renew_tv:   // 超级会员续费
                int type = PreferencesUtils.getInt(UserCenterActivity.this, "type", -1);
                //if (0 == type) {  // 金牌代理
                    String wechat = PreferencesUtils.getString(UserCenterActivity.this, "wechat", "");
                    ToastUtils.ToastMessage(UserCenterActivity.this, "请联系您的邀请人微信为: " + wechat + " 开通会员权限观看内容");
                /*} else {
                    ToastUtils.ToastMessage(UserCenterActivity.this, "暂未开放，敬请期待...");
                }*/
                break;
            case R.id.extreme_member_renew_tv:   // 至尊会员续费
                //ToastUtils.ToastMessage(UserCenterActivity.this, "暂未开放，敬请期待...");
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            toMain();
            return true;
        }
        return false;
    }

    private void toMain() {
        if (!Config.isExitMain) {
            startActivity(new Intent(UserCenterActivity.this, MainActivity.class));
        }
        finish();
    }

    /*private void login() {
        String mobile = PreferencesUtils.getString(UserCenterActivity.this, "mobile");
        final String password = PreferencesUtils.getString(UserCenterActivity.this, "passWord");
        OkHttpUtils.get().url("http://041715.ichengyun.net/api/login?mobile=" + mobile + "&password=" + password).id(1400)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Config.isLogin = false;
                ToastUtils.ToastMessage(UserCenterActivity.this, "网络或服务器异常，请重新登录");
                startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
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
                        userInfo.setPassWord(password);
                        Config.setUserInfo(UserCenterActivity.this, userInfo);   // 保存用户信息
                        refreshData();
                    } else {  // 失败
                        Config.isLogin = false;
                        ToastUtils.ToastMessage(UserCenterActivity.this, "网络或服务器异常，请重新登录");
                        startActivity(new Intent(UserCenterActivity.this, LoginActivity.class));
                        finish();
                    }

                } catch (JSONException e) {
                }
            }
        });
    }*/
}
