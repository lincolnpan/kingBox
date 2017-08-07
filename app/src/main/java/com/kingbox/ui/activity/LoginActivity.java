package com.kingbox.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.service.entity.UserInfo;
import com.kingbox.utils.Config;
import com.kingbox.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 登录
 * Created by Administrator on 2017/7/8.
 */
public class LoginActivity extends BaseActivity {

    @BindView(R.id.user_name_et)
    EditText userNameEt;

    @BindView(R.id.password_et)
    EditText passwordEt;

    @BindView(R.id.forget_password_tv)
    TextView forgetPasswordTv;

    @BindView(R.id.register_tv)
    TextView registerTv;

    @BindView(R.id.login_tv)
    TextView loginTv;

    @BindView(R.id.login_pb)
    ProgressBar loginPB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    100);
        }

        List<Activity> activitieList = mApplication.getActivityList();
        if (null != activitieList && activitieList.size() > 0) {
            Activity tempActivity = null;
            for (Activity activity : activitieList) {
                if (activity instanceof MainActivity) {
                    tempActivity = activity;
                    break;
                }
            }
            if (null != tempActivity)
                tempActivity.finish();
        }
    }

    @OnClick({R.id.login_tv, R.id.forget_password_tv, R.id.register_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_tv:    // 登录
                String mobile = userNameEt.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    ToastUtils.ToastMessage(LoginActivity.this, "请输入手机号码或帐号");
                    return;
                }
                String passwrod = passwordEt.getText().toString().trim();
                if (TextUtils.isEmpty(passwrod)) {
                    ToastUtils.ToastMessage(LoginActivity.this, "请输入密码");
                }
                loginTv.setVisibility(View.GONE);
                loginPB.setVisibility(View.VISIBLE);
                login(mobile, passwrod);
                break;
            case R.id.forget_password_tv:   // 忘记密码
                //startActivity(new Intent(LoginActivity.this, UpdatePasswordActivity.class));
                ToastUtils.ToastMessage(LoginActivity.this, "暂未开放，敬请期待...");
                break;
            case R.id.register_tv:   // 注册
                startActivityForResult(new Intent(LoginActivity.this, RegisterActivity.class), 111);
                break;
        }
    }

    private void login(String mobile, final String password) {
        // 获取用户的代理类型:http://041715.ichengyun.net/api/getUserAgentType?mobile=13011223346&token=98c019f5-42f4-42b7-b356-1f78b33cf78f

        OkHttpUtils.get().url("http://041715.ichengyun.net/api/login?mobile=" + mobile + "&password=" + password).id(1100)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.ToastMessage(LoginActivity.this, "网络异常或服务器异常，登录失败");
                loginTv.setVisibility(View.VISIBLE);
                loginPB.setVisibility(View.GONE);
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
                        Config.setUserInfo(LoginActivity.this, userInfo);   // 保存用户信息
                        Intent intent = new Intent(LoginActivity.this, UserCenterActivity.class);
                        intent.putExtra("userInfo", userInfo);
                        startActivity(intent);
                        finish();
                    } else {  // 失败
                        Config.isLogin = true;
                        ToastUtils.ToastMessage(LoginActivity.this, userInfo.getMsg());
                        loginTv.setVisibility(View.VISIBLE);
                        loginPB.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                }
                //handler.sendEmptyMessage(4444);
                return;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (111 == requestCode && 222 == resultCode) {
            UserInfo userInfo = (UserInfo) data.getSerializableExtra("userInfo");
            userNameEt.setText(userInfo.getMobile());
            passwordEt.setText(userInfo.getPassWord());
            loginTv.performLongClick();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }
}
