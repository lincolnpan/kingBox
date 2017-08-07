package com.kingbox.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.kingbox.R;

public class WelcomeActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                /*String mobile = PreferencesUtils.getString(WelcomeActivity.this, "mobile");
                if (TextUtils.isEmpty(mobile)) {   // 未登录
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                } else {   // 已登录
                    startActivity(new Intent(WelcomeActivity.this, UserCenterActivity.class));
                }*/
                finish();
            }
        },3000);


    }
}
