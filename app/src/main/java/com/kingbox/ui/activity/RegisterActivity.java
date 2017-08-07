package com.kingbox.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.kingbox.R;
import com.kingbox.service.entity.UserInfo;
import com.kingbox.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 注册
 * Created by Administrator on 2017/7/8.
 */
public class RegisterActivity extends BaseActivity {

    @BindView(R.id.account_et)
    EditText accountEt;

    @BindView(R.id.phone_number_et)
    EditText phoneNumberEt;

    @BindView(R.id.password_et)
    EditText passwordEt;

    @BindView(R.id.invitation_code_et)
    EditText invitationCodeEt;

    @BindView(R.id.register_tv)
    TextView registerTv;

    @BindView(R.id.register_layout)
    LinearLayout registerLayout;

    @BindView(R.id.register_pb)
    ProgressBar registerPB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @OnClick(R.id.register_tv)
    public void onClick(){
        String account = accountEt.getText().toString().trim();
        String phoneNumber = phoneNumberEt.getText().toString().trim();
        String passWord = passwordEt.getText().toString().trim();
        String invitationCode = invitationCodeEt.getText().toString().trim();

        if (TextUtils.isEmpty(account)) {
            ToastUtils.ToastMessage(RegisterActivity.this, "请输入帐号");
            return;
        }
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.ToastMessage(RegisterActivity.this, "请输入手机号码");
            return;
        }
        if (TextUtils.isEmpty(passWord)) {
            ToastUtils.ToastMessage(RegisterActivity.this, "请输入密码");
            return;
        }
        if (TextUtils.isEmpty(invitationCode)) {
            ToastUtils.ToastMessage(RegisterActivity.this, "请输入邀请码");
            return;
        }

        registerTv.setVisibility(View.GONE);
        registerPB.setVisibility(View.VISIBLE);
        register(phoneNumber, passWord, account, invitationCode);
    }

    private void register(final String phoneNumber, final String passWord, String account, String invitationCode) {
        //   http://041715.ichengyun.net/api/register?mobile=13011223344&password=1&username=584715&inviteCode=igr3Xl
        OkHttpUtils.get().url("http://041715.ichengyun.net/api/register?mobile=" + phoneNumber + "&password=" + passWord + "&username=" + account + "&inviteCode=" + invitationCode).id(1300)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.ToastMessage(RegisterActivity.this, "网络或服务器异常,注册失败");
                registerPB.setVisibility(View.GONE);
                registerTv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Gson gson = new Gson();
                    UserInfo userInfo = gson.fromJson(jsonObject.toString(), UserInfo.class);
                    if (userInfo.isSuccess() && 0 == userInfo.getCode()) {  // 成功
                        userInfo.setMobile(phoneNumber);
                        userInfo.setPassWord(passWord);
                        Intent intent = new Intent();
                        intent.putExtra("userInfo", userInfo);
                        setResult(222, intent);
                        finish();
                    } else {  // 失败
                        ToastUtils.ToastMessage(RegisterActivity.this, userInfo.getMsg());
                        registerPB.setVisibility(View.GONE);
                        registerTv.setVisibility(View.VISIBLE);
                    }

                } catch (JSONException e) {
                }
                //handler.sendEmptyMessage(4444);
                return;
            }
        });
    }
}
