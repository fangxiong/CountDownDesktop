package com.fax.cddt.bean;



/**
 * Created by fax on 19-6-14.
 */
public class CustomPlugLineBean {
    private int id;
    private boolean containLine;
    private String imageName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isContainLine() {
        return containLine;
    }

    public void setContainLine(boolean containLine) {
        this.containLine = containLine;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
