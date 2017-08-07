package com.kingbox.service.entity;

import java.io.Serializable;

/**
 *  卫视
 * Created by Administrator on 2017/7/10.
 */
public class Tv implements Serializable {

    private boolean Success;

    private String ErrorMsg;

    private int Id;

    private String Name;

    private String Channels;

    private String Remark;

    private String Logo;

    private String Url;

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public String getErrorMsg() {
        return ErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        ErrorMsg = errorMsg;
    }

    public String getChannels() {
        return Channels;
    }

    public void setChannels(String channels) {
        Channels = channels;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }
}
