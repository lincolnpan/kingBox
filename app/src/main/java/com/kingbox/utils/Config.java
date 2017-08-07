package com.kingbox.utils;

import android.content.Context;

import com.kingbox.service.entity.UserInfo;

/**
 * Created by Administrator on 2017/7/11.
 */
public class Config {

    /**
     * 返回主页
     */
    public static boolean isBackHome = false;

    public static boolean isLogin = false;
    public static boolean isExitMain = false;

    public static String recordTime = "RECORD_TIME";
    public static final int TIMES = 180;   // 试看3分钟

    public static void setUserInfo(Context context, UserInfo info){
        PreferencesUtils.putString(context, "expire", info.getExpire());
        PreferencesUtils.putString(context, "mobile", info.getMobile());
        PreferencesUtils.putString(context, "userId", info.getUserId() + "");
        PreferencesUtils.putString(context, "token", info.getToken());
        PreferencesUtils.putString(context, "username", info.getUsername());
        PreferencesUtils.putString(context, "passWord", info.getPassWord());
        PreferencesUtils.putInt(context, "isRecharge", info.getIsRecharge());
        PreferencesUtils.putString(context, "expireTime", info.getExpireTime());
    }

    public static void clearUserInfo(Context context){
        PreferencesUtils.deleteKey(context, "expire");
        PreferencesUtils.deleteKey(context, "mobile");
        PreferencesUtils.deleteKey(context, "userId");
        PreferencesUtils.deleteKey(context, "token");
        PreferencesUtils.deleteKey(context, "username");
        PreferencesUtils.deleteKey(context, "passWord");
        PreferencesUtils.deleteKey(context, "isRecharge");
        PreferencesUtils.deleteKey(context, "expireTime");
        PreferencesUtils.deleteKey(context, "type");
        PreferencesUtils.deleteKey(context, "wechat");
    }
}
