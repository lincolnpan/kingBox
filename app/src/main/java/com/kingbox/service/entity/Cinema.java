package com.kingbox.service.entity;

import java.io.Serializable;

/**
 *  影院
 * Created by Administrator on 2017/7/11.
 */
public class Cinema implements Serializable {

    private String Name;

    private String Logo;

    private String Address;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLogo() {
        return Logo;
    }

    public void setLogo(String logo) {
        Logo = logo;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
