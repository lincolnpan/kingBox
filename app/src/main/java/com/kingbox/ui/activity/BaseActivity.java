package com.kingbox.ui.activity;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.kingbox.application.SuperApplication;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/7/8.
 */
public class BaseActivity extends AppCompatActivity {

    public SuperApplication mApplication = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication = (SuperApplication) this.getApplicationContext();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        ButterKnife.bind(this);

        // 添加每个activity
        mApplication.addActivity(this);
    }
}
