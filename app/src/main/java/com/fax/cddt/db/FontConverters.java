package com.fax.cddt.db;


import android.text.TextUtils;

import com.fax.cddt.bean.ThemeFontBean;
import com.fax.cddt.utils.GsonUtils;

import androidx.room.TypeConverter;

public class FontConverters {
    @TypeConverter
    public static ThemeFontBean fromFontInfo(String str){
        return TextUtils.isEmpty(str) ? null : GsonUtils.parseJsonWithGson(str, ThemeFontBean.class);
    }

    @TypeConverter
    public static String fontInfoToString(ThemeFontBean info){
        return GsonUtils.toJsonWithSerializeNulls(info);
    }
}
