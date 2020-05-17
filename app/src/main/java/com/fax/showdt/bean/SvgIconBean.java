package com.fax.showdt.bean;

import java.util.List;

/**
 * Description:     java类作用描述
 * Author:          fax
 * CreateDate:      2020-05-06 21:34
 * Email:           fxiong1995@gmail.com
 */
public class SvgIconBean {


    private IconBean icon;

    private Properties properties;

    private String path;


    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setIcon(IconBean icon) {
        this.icon = icon;
    }

    public IconBean getIcon() {
        return icon;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public class IconBean{
        private List<String> paths;

        private boolean isMulticolor;

        private List<String> tags;

        private int defaultCode;

        private int grid;

        public List<String> getPaths() {
            return paths;
        }

        public void setPaths(List<String> paths) {
            this.paths = paths;
        }

        public boolean isMulticolor() {
            return isMulticolor;
        }

        public void setMulticolor(boolean multicolor) {
            isMulticolor = multicolor;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public int getDefaultCode() {
            return defaultCode;
        }

        public void setDefaultCode(int defaultCode) {
            this.defaultCode = defaultCode;
        }

        public int getGrid() {
            return grid;
        }

        public void setGrid(int grid) {
            this.grid = grid;
        }
    }

    public class Properties
    {
        private int order;

        private String ligatures;

        private int prevSize;

        private String name;

        private int id;

        private int code;

        public void setOrder(int order){
            this.order = order;
        }
        public int getOrder(){
            return this.order;
        }
        public void setLigatures(String ligatures){
            this.ligatures = ligatures;
        }
        public String getLigatures(){
            return this.ligatures;
        }
        public void setPrevSize(int prevSize){
            this.prevSize = prevSize;
        }
        public int getPrevSize(){
            return this.prevSize;
        }
        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return this.name;
        }
        public void setId(int id){
            this.id = id;
        }
        public int getId(){
            return this.id;
        }
        public void setCode(int code){
            this.code = code;
        }
        public int getCode(){
            return this.code;
        }
    }

}
