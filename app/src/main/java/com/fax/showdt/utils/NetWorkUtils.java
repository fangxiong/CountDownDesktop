package com.fax.showdt.utils;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Author: fax
 * Email: fxiong1995@gmail.com
 * Date: 19-8-8
 * Description:
 */


public class NetWorkUtils {
    public static final String MODE_WIFI = "wifi";
    public static final String MODE_4G_ = "4g";
    public static final String MODE_3G = "3g";
    public static final String MODE_2G = "2g";
    public static final String MODE_UNKNOWN = "unknown";

    public static String getNetWorkModel(Context context) {
        if (isWifiAvailable(context)) {
            return MODE_WIFI;
        } else if (isNetwork4G(context)) {
            return MODE_4G_;
        } else if (isNetwork3G(context)) {
            return MODE_3G;
        } else if (isNetwork2G(context)) {
            return MODE_2G;
        } else {
            return MODE_UNKNOWN;
        }
    }

    /**
     * 准确判断sim类别 必须流量开关是打开状态，wifi开关为关闭状态 判断手机卡 是4G，3G,还是2G卡 返回0表示无法判断
     *
     * @param context
     * @return
     */
    public static String getSimNetworkType(Context context) {
        String type = "";
        switch (getMobileNetworkType(context)) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                type = "2g";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                type = "3g";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                type = "4g";
                break;
            default:
                type = isWifiAvailable(context) ? "wifi" : "0";
                break;
        }
        return type;
    }

    ;

    /**
     * gprs开关是否打开
     *
     * @return
     */
    public static boolean getMobileDataStatus(Context context) {
        ConnectivityManager cm;
        cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        Class cmClass = cm.getClass();
        Class[] argClasses = null;
        Object[] argObject = null;
        Boolean isOpen = false;
        try {
            Method method = cmClass.getMethod("getMobileDataEnabled",
                    argClasses);
            isOpen = (Boolean) method.invoke(cm, argObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isOpen;
    }

    /**
     * 打开或者关闭 移动数据流量
     *
     * @param context
     * @param enabled
     */
    public static void toggleMobileData(Context context, boolean enabled) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Method setMobileDataEnabl;
        try {
            setMobileDataEnabl = connectivityManager.getClass()
                    .getDeclaredMethod("setMobileDataEnabled", boolean.class);
            setMobileDataEnabl.invoke(connectivityManager, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查WIFI是否已经连接
     */
    public static boolean isWifiAvailable(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null) {
            return false;
        }
        NetworkInfo wifiInfo = conMan
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (wifiInfo != null && wifiInfo.getState() == State.CONNECTED);
    }

    public static State getNetWorkInfoState(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = conMan
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo == null) {
            return State.UNKNOWN;
        }
        return wifiInfo.getState();
    }

    /**
     * 检查网络是否连接，WIFI或者手机网络其一
     */
    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager conMan = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMan == null) {
            return false;
        }


        NetworkInfo activeInfo = conMan.getActiveNetworkInfo();
        if (null == activeInfo) {
            return false;
        }
        if (activeInfo.getState() == State.CONNECTED) {
            return true;
        }
        return false;
    }

    /**
     * 检查网络状态并返回当前状态，网络不可用时提示
     */
    public static boolean checkNetworkStat(Context ctx) {
        boolean isAvailable = isNetworkAvailable(ctx);
        return isAvailable;
    }

    public static boolean isNetwork2G(Context context) {
        int subType = getMobileNetworkType(context);
        return (subType == TelephonyManager.NETWORK_TYPE_CDMA
                || subType == TelephonyManager.NETWORK_TYPE_EDGE
                || subType == TelephonyManager.NETWORK_TYPE_GPRS
                || subType == TelephonyManager.NETWORK_TYPE_1xRTT || subType == TelephonyManager.NETWORK_TYPE_IDEN);
    }

    public static boolean isNetwork3G(Context context) {
        int subType = getMobileNetworkType(context);

        boolean ret = (subType == TelephonyManager.NETWORK_TYPE_UMTS
                || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                || subType == TelephonyManager.NETWORK_TYPE_EVDO_B
                || subType == TelephonyManager.NETWORK_TYPE_HSPA
                || subType == TelephonyManager.NETWORK_TYPE_EHRPD || subType == TelephonyManager.NETWORK_TYPE_HSUPA); // TODO:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            ret = ret || subType == TelephonyManager.NETWORK_TYPE_HSPAP;
        }
        return ret; // 移动3G如何判断?
    }

    public static boolean isNetwork4G(Context context) {
        int subType = getMobileNetworkType(context);
        return subType == TelephonyManager.NETWORK_TYPE_LTE;
    }

    public static int getMobileNetworkType(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            return info.getSubtype();
        }
        return -1;
    }

    /**
     * 获取当前的网络状态 null：没有网络 wifi：WIFI网络 wap：wap网络 net：net网络
     *
     * @param context
     * @return
     */
    public static String getAPNType(Context context) {
        String netType = null;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (!TextUtils.isEmpty(networkInfo.getExtraInfo())
                    && networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                netType = "net";
            } else {
                netType = "wap";
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = "wifi";
        } else {
            netType = "其它";
        }
        return netType;
    }

    public static NetworkInfo getNetWorkInfo(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo();
    }

    public static interface WifiStateListener {

        void onWifiStateChanged(State networkState);
    }

    private static List<WeakReference<WifiStateListener>> mListeners = new ArrayList<WeakReference<WifiStateListener>>();

    public static void addWifiStateListener(WifiStateListener listener) {
        if (null == listener) {
            return;
        }
        if (null == mListeners) {
            mListeners = new ArrayList<WeakReference<WifiStateListener>>();
        }

        synchronized (mListeners) {
            for (WeakReference<WifiStateListener> ref : mListeners) {
                if (ref.get() == listener) {
                    return;
                }
            }
            WeakReference<WifiStateListener> ref = new WeakReference<WifiStateListener>(
                    listener);
            mListeners.add(ref);
        }
    }

    public static void removeWifiStateListener(WifiStateListener listener) {
        if (null == listener || null == mListeners) {
            return;
        }

        synchronized (mListeners) {
            Iterator<WeakReference<WifiStateListener>> iter = mListeners
                    .iterator();

            while (iter.hasNext()) {
                WeakReference<WifiStateListener> ref = iter.next();
                if (null == ref.get()) {
                    iter.remove();
                    continue;
                }
                if (ref.get() == listener) {
                    ref.clear();
                    iter.remove();
                    break;
                }
            }
        }
    }

    public static void notifyWifiStateChange(NetworkInfo wifiInfo) {
        synchronized (mListeners) {
            for (WeakReference<WifiStateListener> ref : mListeners) {
                final WifiStateListener listener = ref.get();
                if (null != listener) {
                    listener.onWifiStateChanged(wifiInfo.getState());
                }
            }
        }
    }

}