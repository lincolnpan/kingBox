package com.kingbox.application;

import android.app.Activity;
import android.app.Application;

import com.kingbox.utils.CrashHandler;
import com.tencent.smtt.sdk.QbSdk;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/10.
 */
public class SuperApplication extends Application {

    /**
     * 保存activity集合
     */
    protected List<Activity> activityStack = new LinkedList<>();

    @Override
    public void onCreate() {
        super.onCreate();

        // 异常捕获(crash)
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());

        //初始化X5内核
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                //x5内核初始化完成回调接口，此接口回调并表示已经加载起来了x5，有可能特殊情况下x5内核加载失败，切换到系统内核。
            }

            @Override
            public void onViewInitFinished(boolean b) {
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
            }
        });
    }

    /**
     * 添加新的Activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {

        if (activityStack.size() > 0) {
            if (!activityStack.contains(activity)) {
                activityStack.add(activity);
            }
        } else {
            activityStack.add(activity);
        }
    }

    /**
     * 获取所有activity
     *
     * @return
     */
    public List<Activity> getActivityList() {
        return activityStack;
    }

    /**
     * finish所有activity
     */
    public void finishActivity() {
        for (Activity activity : activityStack) {
            if (null != activity) {
                activity.finish();
            }
        }
    }
}
