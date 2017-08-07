package com.kingbox.ui.activity;

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
import com.kingbox.utils.PreferencesUtils;
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
 * Created by Administrator on 2017/7/8.
 */
public class UpdatePasswordActivity extends BaseActivity {

    @BindView(R.id.old_password_et)
    EditText oldPasswordEt;

    @BindView(R.id.new_password_et)
    EditText newPasswordEt;

    @BindView(R.id.new_password1_et)
    EditText newPassword1Et;

    @BindView(R.id.update_tv)
    TextView updateTv;

    @BindView(R.id.forget_password_layout)
    LinearLayout forgetPasswordLayout;

    @BindView(R.id.update_pb)
    ProgressBar updatePB;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        ButterKnife.bind(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

    }

    @OnClick(R.id.update_tv)
    public void onClick() {
        String oldPassWord = oldPasswordEt.getText().toString().trim();
        String newPassWord = newPasswordEt.getText().toString().trim();
        String newPassWord1 = newPassword1Et.getText().toString().trim();
        if (TextUtils.isEmpty(oldPassWord)) {
            ToastUtils.ToastMessage(UpdatePasswordActivity.this, "请输入原密码");
            return;
        }
        if (TextUtils.isEmpty(newPassWord)) {
            ToastUtils.ToastMessage(UpdatePasswordActivity.this, "请输入新密码");
            return;
        }
        if (TextUtils.isEmpty(newPassWord1)) {
            ToastUtils.ToastMessage(UpdatePasswordActivity.this, "请输入确认新密码");
            return;
        }
        if (!newPassWord.equals(newPassWord1)) {
            ToastUtils.ToastMessage(UpdatePasswordActivity.this, "新密码与确认新密码不一致");
            return;
        }
        updateTv.setVisibility(View.GONE);
        updatePB.setVisibility(View.VISIBLE);
        update(oldPassWord, newPassWord);
    }

    private void update(String oldPassWord, final String newPassWord) {
        //http://041715.ichengyun.net/api/updatePassword?mobile=13011223344&password=1&newPassword=2&token=f8c3bc25-bd8c-4388-b4f4-db9ced0cdc3a
        String mobile = PreferencesUtils.getString(UpdatePasswordActivity.this, "mobile");
        String token = PreferencesUtils.getString(UpdatePasswordActivity.this, "token");
        OkHttpUtils.get().url("http://041715.ichengyun.net/api/updatePassword?mobile=" + mobile + "&password=" + oldPassWord + "&newPassword=" + newPassWord + "&token=" + token).id(1200)   // 请求Id
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                ToastUtils.ToastMessage(UpdatePasswordActivity.this, "网络或服务器异常,修改失败");
                updateTv.setVisibility(View.VISIBLE);
                updatePB.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    Gson gson = new Gson();
                    UserInfo userInfo = gson.fromJson(jsonObject.toString(), UserInfo.class);
                    if (userInfo.isSuccess() && 0 == userInfo.getCode()) {  // 成功
                        PreferencesUtils.putString(UpdatePasswordActivity.this, "passWord", newPassWord);
                        ToastUtils.ToastMessage(UpdatePasswordActivity.this, "修改成功");
                        finish();
                    } else {  // 失败
                        ToastUtils.ToastMessage(UpdatePasswordActivity.this, userInfo.getMsg());
                        updateTv.setVisibility(View.VISIBLE);
                        updatePB.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                }
            }
        });
    }
}
