package com.fax.showdt.utils;

import android.text.TextUtils;
import android.util.Log;

import com.fax.lib.config.ConfigManager;
import com.fax.showdt.bean.CustomWidgetConfig;
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

    public static CustomWidgetConfig getWidgetDataFromId(String mKey, String mapKey) {
        Map map = getHashMapData(mapKey);
        Iterator iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = (String) entry.getKey();
            if (key.equals(mKey)) {
                String val = (String) entry.getValue();
                return GsonUtils.parseJsonWithGson(val,CustomWidgetConfig.class);
            }
        }
        return null;
    }

    public static void putWidgetDataWithId(String mKey, String mValue,String mapKey) {
        Map map = getHashMapData(mapKey);
        map.put(mKey, mValue);
        putHashMapData(map,mapKey);
        Log.e("test_put1:",mValue);
        Log.e("test_put2:",getWidgetDataFromId(mKey,mapKey).toJSONString());
    }

    public static void deleteWidgetDataFromId(String mKey,String mapKey) {
        Map map = getHashMapData(mapKey);
        map.remove(mKey);
        putHashMapData(map,mapKey);
    }
}
