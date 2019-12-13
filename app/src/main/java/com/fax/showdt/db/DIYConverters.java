package com.fax.showdt.db;


import com.fax.showdt.bean.DrawablePlugBean;
import com.fax.showdt.bean.LinePlugBean;
import com.fax.showdt.bean.ProgressPlugBean;
import com.fax.showdt.bean.ShortcutIconBean;
import com.fax.showdt.bean.TextPlugBean;
import com.fax.showdt.utils.GsonUtils;

import java.util.List;

import androidx.room.TypeConverter;

public class DIYConverters {

    public static class TextPlugListConverters {
        @TypeConverter
        public static List<TextPlugBean> fromTextPlugList(String str){
            return GsonUtils.parseJsonArrayWithGson(str, TextPlugBean.class);
        }

        @TypeConverter
        public static String TextPlugListToString(List<TextPlugBean> list){
            return GsonUtils.toJsonArrayWithExpose(list);
        }
    }

    public static class LinePlugListConverters {
        @TypeConverter
        public static List<LinePlugBean> fromLinePlugList(String str){
            return GsonUtils.parseJsonArrayWithGson(str, LinePlugBean.class);
        }

        @TypeConverter
        public static String LinePlugListToString(List<LinePlugBean> list){
            return GsonUtils.toJsonArrayWithExpose(list);
        }
    }

    public static class ProgressPlugListConverters {
        @TypeConverter
        public static List<ProgressPlugBean> fromProgressPlugList(String str){
            return GsonUtils.parseJsonArrayWithGson(str, ProgressPlugBean.class);
        }

        @TypeConverter
        public static String ProgressPlugListToString(List<ProgressPlugBean> list){
            return GsonUtils.toJsonArrayWithExpose(list);
        }
    }

    public static class DrawablePlugListConverters {
        @TypeConverter
        public static List<DrawablePlugBean> fromDrawablePlugList(String str){
            return GsonUtils.parseJsonArrayWithGson(str, DrawablePlugBean.class);
        }

        @TypeConverter
        public static String IconPlugListToString(List<DrawablePlugBean> list){
            return GsonUtils.toJsonArrayWithExpose(list);
        }
    }


    public static class ShortcutIconListConverters {
        @TypeConverter
        public static List<ShortcutIconBean> fromShortcutPlugList(String str){
            return GsonUtils.parseJsonArrayWithGson(str, ShortcutIconBean.class);
        }

        @TypeConverter
        public static String ShortcutListToString(List<ShortcutIconBean> list){
            return GsonUtils.toJsonArrayWithExpose(list);
        }
    }
}
