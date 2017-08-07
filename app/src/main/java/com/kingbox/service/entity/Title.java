package com.kingbox.service.entity;

import com.kingbox.utils.TypeFactory;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/7/12.
 */
public class Title implements Visitable, Serializable {

    private String name;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private int type;

    private List<Banner> bannerList;

    public List<Banner> getBannerList() {
        return bannerList;
    }

    public void setBannerList(List<Banner> bannerList) {
        this.bannerList = bannerList;
    }

    public Title(String name, int type){
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    @Override
    public int type(TypeFactory typeFactory) {
        return typeFactory.type(this);
    }
}

