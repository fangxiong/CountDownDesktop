package com.fax.cddt.bean;



public class LinePlugBean extends BasePlugBean{

    private final int vertical = 1;
    private final int horizontal = 2;

    private int style;
    private float size;
    private int lineId;
    private float scaleRatio;
    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public boolean isVertical(){
        return style == vertical;
    }

    public int getLineId() {
        return lineId;
    }

    public void setLineId(int lineId) {
        this.lineId = lineId;
    }

    public float getScaleRatio() {
        return scaleRatio;
    }

    public void setScaleRatio(float scaleRatio) {
        this.scaleRatio = scaleRatio;
    }
}
