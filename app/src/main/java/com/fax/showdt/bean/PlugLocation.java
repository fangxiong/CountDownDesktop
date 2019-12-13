package com.fax.showdt.bean;


public class PlugLocation extends Bean {
    private float x;
    private float y;

    public PlugLocation(){}
    public PlugLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
