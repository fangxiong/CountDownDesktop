package com.fax.showdt.bean;

/**
 * Created by fax on 19-4-17.
 */

public class CustomPlugTextBean extends Bean {
    private int id;
    private String tag;
    private String hint;
    private boolean isHead;
    private boolean singleLine;
    private boolean doubleLine;
    private String category;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }

    public boolean isSingleLine() {
        return singleLine;
    }

    public void setSingleLine(boolean singleLine) {
        this.singleLine = singleLine;
    }

    public boolean isDoubleLine() {
        return doubleLine;
    }

    public void setDoubleLine(boolean doubleLine) {
        this.doubleLine = doubleLine;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
