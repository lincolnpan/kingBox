package com.kingbox.service.entity;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/15.
 */
public class Banner implements Serializable {

    private String Pic;

    private String Url;

    public String getPic() {
        return Pic;
    }

    public void setPic(String pic) {
        Pic = pic;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }
}
