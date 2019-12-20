package com.fax.showdt.utils;

import android.text.TextUtils;

import com.fax.lib.config.ConfigManager;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-12-20
 * Description:
 */
public class WidgetDataHandlerUtils {


    public static boolean putHashMapData(Map<String, String> map, String key) {
        boolean result;
        try {
            Gson gson = new Gson();
            String json = gson.toJson(map);
            ConfigManager.getMainConfig().putString(key, json);
            result = true;
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }


    public static HashMap<String, String> getHashMapData(String key) {
        String json = ConfigManager.getMainConfig().getString(key, "");
        HashMap<String, String> map = new HashMap<>();
        Gson gson = new Gson();
        if (!TextUtils.isEmpty(json)) {
            map = (HashMap<String, String>) gson.fromJson(json, HashMap.class);
        }

        return map;
    }

    public static String getWidgetDataFromId(String mKey,String mapKey) {
        Map map = getHashMapData(mapKey);
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            if (key.equals(mKey)) {
                String val = (String) entry.getValue();
                return val;
            }
        }
        return null;
    }

    public static void putWidgetDataWithId(String mKey, String mValue,String mapKey) {
        Map map = getHashMapData(mapKey);
        map.put(mKey, mValue);
        putHashMapData(map,mapKey);
    }

    public static void deleteWidgetDataFromId(String mKey,String mapKey) {
        Map map = getHashMapData(mapKey);
        map.remove(mKey);
        putHashMapData(map,mapKey);
    }
}
