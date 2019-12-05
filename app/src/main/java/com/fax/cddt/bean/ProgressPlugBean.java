package com.fax.cddt.bean;


/**
 * Created by showzeng on 2019-09-02.
 * email: kingstageshow@gmail.com
 *
 * description: 进度条配置实体类
 */
public class ProgressPlugBean extends BasePlugBean {

    private float size;
    private int progressId;
    private float scaleRatio;
    private long startTime;
    private long targetTime;

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getProgressId() {
        return progressId;
    }

    public void setProgressId(int progressId) {
        this.progressId = progressId;
    }

    public float getScaleRatio() {
        return scaleRatio;
    }

    public void setScaleRatio(float scaleRatio) {
        this.scaleRatio = scaleRatio;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getTargetTime() {
        return targetTime;
    }

    public void setTargetTime(long targetTime) {
        this.targetTime = targetTime;
    }

}
