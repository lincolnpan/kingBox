package com.kingbox.service.entity;

import com.kingbox.utils.TypeFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 直播秀场
 * Created by Administrator on 2017/7/11.
 */
public class LiveField implements Visitable, Serializable {

    public LiveField(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    private int type;

    private String msg;

    private String img;
    private String roomName;

    public String getLiveUrl() {
        return liveUrl;
    }

    public void setLiveUrl(String liveUrl) {
        this.liveUrl = liveUrl;
    }

    private String liveUrl;

    private boolean isWebPlay;

    public boolean isWebPlay() {
        return isWebPlay;
    }

    public void setWebPlay(boolean webPlay) {
        isWebPlay = webPlay;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private List<LiveField> liveFieldList = new ArrayList<>();

    public List<LiveField> getLiveFieldList() {
        return liveFieldList;
    }

    public void setLiveFieldList(List<LiveField> liveFieldList) {
        this.liveFieldList = liveFieldList;
    }

    @Override
    public int type(TypeFactory typeFactory) {
        return typeFactory.type(this, type);
    }
}
