package com.kingbox.application;

import android.app.Activity;
import android.app.Application;

import com.kingbox.utils.CrashHandler;

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
