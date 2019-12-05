package com.fax.cddt.bean;


/**
 * Created by showzeng on 2019-09-02.
 * email: kingstageshow@gmail.com
 *
 * description: 自定义桌面插件-进度条样式 Bean 对象
 */
public class CustomPlugProgressBean {

    private int id;
    private String color;
    private String imageName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
