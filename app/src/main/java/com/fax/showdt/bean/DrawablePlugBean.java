package com.fax.showdt.bean;


/**
 * Created by fax on 19-4-25.
 */

public class DrawablePlugBean extends BasePlugBean {
    private String drawablePath;
    private String name;
    private String svgName;
    private boolean isShowFrame;

    public String getDrawablePath() {
        return drawablePath;
    }

    public void setDrawablePath(String drawablePath) {
        this.drawablePath = drawablePath;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSvgName() {
        return svgName == null ? "" : svgName;
    }

    public void setSvgName(String svgName) {
        this.svgName = svgName;
    }

    public boolean isShowFrame() {
        return isShowFrame;
    }

    public void setShowFrame(boolean showFrame) {
        isShowFrame = showFrame;
    }
}