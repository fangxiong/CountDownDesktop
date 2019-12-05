package com.fax.cddt.utils;


import android.content.Context;
import android.graphics.Typeface;

import java.io.File;
import java.util.Hashtable;

public class FontCache {

    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    public static Typeface get(String name, Context context) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                if(name.equals("")){
                    tf = Typeface.DEFAULT;
                }else {
                    tf = Typeface.createFromAsset(context.getAssets(), name);
                }
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }

    public static Typeface get(String name, File file) {
        Typeface tf = fontCache.get(name);
        if(tf == null) {
            try {
                tf = Typeface.createFromFile(file);
            }
            catch (Exception e) {
                return null;
            }
            fontCache.put(name, tf);
        }
        return tf;
    }
}