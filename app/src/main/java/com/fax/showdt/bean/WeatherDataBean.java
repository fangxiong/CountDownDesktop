package com.fax.showdt.bean;

import java.util.List;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-9-5
 * Description:
 */
public class WeatherDataBean extends Bean {

    private int status;
    private int count;
    private String info;
    private int infocode;
    private List<WeatherDetailBean> lives;

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public void setInfocode(int infocode) {
        this.infocode = infocode;
    }

    public int getInfocode() {
        return infocode;
    }

    public void setLives(List<WeatherDetailBean> lives) {
        this.lives = lives;
    }

    public List<WeatherDetailBean> getLives() {
        return lives;
    }

}
