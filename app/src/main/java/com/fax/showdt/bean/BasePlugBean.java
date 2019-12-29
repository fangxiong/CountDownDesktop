package com.fax.showdt.bean;


public class BasePlugBean extends Bean {
    private String id;
    private float scale;
    private String color;
    private float angle;

    private PlugLocation location;
    private int width;
    private int height;
    private String jumpAppPath;
    private String mJumpContent;
    private String appName;
    private float left;
    private float top;
    private float right;
    private float bottom;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public String getColor() {
        return color == null ? "" : color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public PlugLocation getLocation() {
        return location;
    }

    public void setLocation(PlugLocation location) {
        this.location = location;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getJumpAppPath() {
        return jumpAppPath;
    }

    public void setJumpAppPath(String jumpAppPath) {
        this.jumpAppPath = jumpAppPath;
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getRight() {
        return right;
    }

    public void setRight(float right) {
        this.right = right;
    }

    public float getBottom() {
        return bottom;
    }

    public void setBottom(float bottom) {
        this.bottom = bottom;
    }

    public String getJumpContent() {
        return mJumpContent;
    }

    public void setJumpContent(String mJumpContent) {
        this.mJumpContent = mJumpContent;
    }

    public String getAppName() {
        return appName == null ? "" : appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getAngle() {
        return angle;
    }
}
