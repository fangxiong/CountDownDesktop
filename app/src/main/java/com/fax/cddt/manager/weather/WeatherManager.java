package com.fax.cddt.manager.weather;

import android.os.AsyncTask;

import com.fax.cddt.bean.WeatherDataBean;
import com.fax.cddt.bean.WeatherDetailBean;
import com.fax.cddt.manager.location.LocationManager;
import com.fax.cddt.utils.TimeUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-9-4
 * Description:获取用户当地城市天气  获取的定位由高德定位服务给予
 */
public class WeatherManager {
    public static WeatherManager mInstance;
    private final String WEATHER_URL = "https://restapi.amap.com/v3/weather/weatherInfo";
    private final String WEATHER_KEY = "3fac03be7fad5955928b6cf1e7419977";
    public static String temperature = "26℃";
    public static String weather = "阴";
    private String city_key = "";
    private long lastUpdateTime = 0;
    private final int UPDATE_GAP = 3600000;

    private WeatherManager() {
    }

    public static WeatherManager getInstance() {
        if (mInstance == null) {
            synchronized (WeatherManager.class) {
                if (mInstance == null) {
                    mInstance = new WeatherManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 在获取用户当地天气之前 必须先获取其定位的地址
     * 执行这个函数之前必须要去执行LocationManager的startLocation()方法去获取定位
     */
    public synchronized void starGetWeather() {
        //当启动天气服务之前 需开启定位服务,获取其城市编码
        LocationManager.getInstance().startLocation();
        Task<WeatherDataBean> task = new Task<WeatherDataBean>() {
            @Override
            public WeatherDataBean doInBackground() throws InterruptedException {

                //当城市编码改变了 再去请求
                city_key = LocationManager.LOCATION_CITY_CODE;
                Map<String, Object> map = new HashMap<>();
                map.put("key", WEATHER_KEY);
                map.put("city", city_key);
                map.put("extensions", "base");
                try {
                    String result = HttpUtils.doGetStringWithParams(WEATHER_URL, map);
                    DebugLog.i("test_weather_response:", result);
                    if (result != null) {
                        return JsonUtils.fromJson(result, WeatherDataBean.class);
                    }
                    return null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void onSuccess(WeatherDataBean result) {
                if (result != null && result.getStatus() == 1) {
                    List<WeatherDetailBean> mList = result.getLives();
                    if (mList != null && mList.size() > 0) {
                        WeatherDetailBean detailBean = mList.get(0);
                        if (detailBean != null) {
                            temperature = detailBean.getTemperature() + "℃";
                            weather = detailBean.getWeather();
                        }
                    }

                }
            }
        };
        long timeGap = System.currentTimeMillis() - lastUpdateTime;
        if (!city_key.equals(LocationManager.LOCATION_CITY_CODE) || lastUpdateTime == 0 || timeGap > UPDATE_GAP) {
            lastUpdateTime = System.currentTimeMillis() ;
            TaskScheduler.execute(task);
        }

    }
}
