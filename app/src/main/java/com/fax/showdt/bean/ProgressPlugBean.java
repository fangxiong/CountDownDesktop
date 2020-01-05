package com.fax.showdt.bean;


import com.fax.showdt.view.sticker.ProgressSticker;

public class ProgressPlugBean extends BasePlugBean {

    private int progressId;
    private float percent;
    private int progressHeight;
    private String foreColor;
    private String bgColor;
    @ProgressSticker.ProgressDrawType
    private String drawType;
    @ProgressSticker.ProgressType
    private String progressType;
    @ProgressSticker.Progress
    private String progress;

    public void setPercent(float percent) {
        this.percent = percent;
    }

    public int getProgressHeight() {
        return progressHeight;
    }

    public void setProgressHeight(int progressHeight) {
        this.progressHeight = progressHeight;
    }

    public String getForeColor() {
        return foreColor;
    }

    public void setForeColor(String foreColor) {
        this.foreColor = foreColor;
    }

    public String getBgColor() {
        return bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = bgColor;
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

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getProgress() {
        return progress;
    }

    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }


}
