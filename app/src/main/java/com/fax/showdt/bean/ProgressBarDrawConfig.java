package com.fax.showdt.bean;

import com.fax.showdt.view.sticker.ProgressSticker;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-31
 * Description:
 */
public class ProgressBarDrawConfig extends Bean{
    private float percent;
    private float width;
    private float height;
    private float progressHeight;
    private String progressBgColor;
    private String progressForeColor;
    @ProgressSticker.ProgressDrawType
    private  String drawType;
    @ProgressSticker.ProgressType
    private String progressType;

    public float getPercent() {
        return percent;
    }

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setProgressHeight(float progressHeight) {
        this.progressHeight = progressHeight;
    }

    public float getProgressHeight() {
        return progressHeight;
    }

    public String getProgressBgColor() {
        return progressBgColor;
    }

    public void setProgressBgColor(String progressBgColor) {
        this.progressBgColor = progressBgColor;
    }

    public String getProgressForeColor() {
        return progressForeColor;
    }

    public void setProgressForeColor(String progressForeColor) {
        this.progressForeColor = progressForeColor;
    }

    public String getDrawType() {
        return drawType;
    }

    public void setDrawType(String drawType) {
        this.drawType = drawType;
    }

    public String getProgressType() {
        return progressType;
    }

    public void setProgressType(String progressType) {
        this.progressType = progressType;
    }
}
