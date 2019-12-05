package com.fax.cddt.manager.location;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.fax.cddt.AppContext;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-9-4
 * Description:通过高德定位sdk获取用户当前位置信息
 */
public class LocationManager {
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener ;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    public static LocationManager mInstance;
    private LocationChangedCallback mLocationChangedCallback;
    public static String LOCATION_CITY_CODE = "";
    public static String LOCATION = "北京市";
    private final int UPDATE_GAP = 3600000;


    private LocationManager(){}
    public static LocationManager getInstance(){
        if(mInstance == null){
            synchronized (LocationManager.class){
                if(mInstance == null){
                    mInstance = new LocationManager();
                }
            }
        }
        return mInstance;
    }

    public void setLocationChangedCallback(LocationChangedCallback mCallback){
        this.mLocationChangedCallback = mCallback;
    }

    /**
     * 开始定位服务
     */
    public synchronized void startLocation(){
        if(mLocationClient != null && mLocationClient.isStarted()){
            return;
        }
        //初始化定位
        mLocationClient = new AMapLocationClient(AppContext.get());
        //设置定位回调监听
        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if(aMapLocation != null){
                    if(aMapLocation.getErrorCode() == 0){
                        LOCATION_CITY_CODE = aMapLocation.getAdCode();
                        LOCATION = aMapLocation.getCity();
                        if(mLocationChangedCallback != null){
                            mLocationChangedCallback.locChanged(true,aMapLocation.getCity(),aMapLocation.getCityCode());
                        }
                    }else {
                        if(mLocationChangedCallback != null){
                            mLocationChangedCallback.locChanged(false,"0","0");
                        }
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    }
                }
            }
        };
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Transport);
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        option.setInterval(UPDATE_GAP);
        option.setNeedAddress(true);
        if(null != mLocationClient){
            mLocationClient.setLocationOption(option);
            //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
            mLocationClient.stopLocation();
            mLocationClient.startLocation();
        }
    }

    /**
     * 停止定位服务
     */
    public void stopLocation(){
        if(mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            //onDestroy后需要重新实例化AMapLocationClient,之前的对象需要手动置空
            mLocationClient = null;
        }
    }

    public interface LocationChangedCallback{
        void locChanged(boolean reqStatus, String location, String locCode);
    }

}
