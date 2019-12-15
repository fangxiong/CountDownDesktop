package com.fax.showdt.manager.weather;

import android.os.Handler;
import android.util.Log;

import com.fax.showdt.bean.WeatherDataBean;
import com.fax.showdt.bean.WeatherDetailBean;
import com.fax.showdt.manager.location.LocationManager;
import com.fax.showdt.utils.GsonUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-9-4
 * Description:获取用户当地城市天气  获取的定位由高德定位服务给予
 */
public class WeatherManager {
    public static WeatherManager mInstance;
    private final String WEATHER_URL = "https://restapi.amap.com/v3/weather/weatherInfo";
    private final String WEATHER_KEY = "a8f5dc18b7c13ae86f616a82a1240ced";
    public static String temperature = "26℃";
    public static String weather = "阴";
    private String city_key = "";
    private long lastUpdateTime = 0;
    private final int UPDATE_GAP = 3600000;
    private Disposable disposable;

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
        long timeGap = System.currentTimeMillis() - lastUpdateTime;
        if (!city_key.equals(LocationManager.LOCATION_CITY_CODE) || lastUpdateTime == 0 || timeGap > UPDATE_GAP) {
            lastUpdateTime = System.currentTimeMillis();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    reqWeatherData();
                }
            },2000);
        }

    }

    private void reqWeatherData() {
        //当启动天气服务之前 需开启定位服务,获取其城市编码
        LocationManager.getInstance().startLocation();
        Observable.create(new ObservableOnSubscribe<WeatherDataBean>() {
            @Override
            public void subscribe(ObservableEmitter<WeatherDataBean> e) throws Exception {
                //当城市编码改变了 再去请求
                city_key = LocationManager.LOCATION_CITY_CODE;
                Map<String, String> map = new HashMap<>();
                map.put("key", WEATHER_KEY);
                map.put("city", city_key);
                Log.i("test_param:","city:"+city_key);
                Response response = OkHttpUtils.get().url(WEATHER_URL).params(map).build().execute();
                if (response != null) {
//                    Log.i("test_param:",response.body().string());
                    String result = response.body().string();
                    WeatherDataBean bean = GsonUtils.parseJsonWithGson(result,WeatherDataBean.class);
                    Log.i("test_param:",bean.toJSONString());
                    if(bean != null) {
                    e.onNext(bean);
                    }else {
                        e.onError(new Exception("请求失败"));
                    }
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<WeatherDataBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(WeatherDataBean result) {
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
                        if(disposable != null && !disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if(disposable != null && !disposable.isDisposed()){
                            disposable.dispose();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
