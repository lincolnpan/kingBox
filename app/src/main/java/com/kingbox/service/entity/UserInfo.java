package com.kingbox.service.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/27.
 */

public class UserInfo implements Serializable {
    private int code;

    private boolean success;

    private String msg;

    /**
     * 到期时间
     */
    private String expire;   //  token到期时间

    /**
     * 帐号到期时间
     */
    private String expireTime;

    private String mobile;

    private int userId;

    private String token;

    private String username;

    private String passWord;

    private int type = -1;   //type:0， 表示是金牌代理,type:1,表示是渠道代理

    private int isRecharge;  //是否有充值记录(0:无,1:有)

    private String wechat;

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public int getIsRecharge() {
        return isRecharge;
    }

    public void setIsRecharge(int isRecharge) {
        this.isRecharge = isRecharge;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getExpire() {
        return expire;
    }

    public void setExpire(String expire) {
        this.expire = expire;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
