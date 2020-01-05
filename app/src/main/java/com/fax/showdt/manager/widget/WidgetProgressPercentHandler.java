package com.fax.showdt.manager.widget;

import android.util.Log;

import com.fax.showdt.utils.CustomPlugUtil;
import com.fax.showdt.utils.TimeUtils;


public class WidgetProgressPercentHandler {


    /**
     * 获取进度条的进度
     * @param progress
     * @return
     */
    public static float getProgressPercent(String progress){
        switch (progress){
            case WidgetProgress.BATTERY:{
                return getSystemBatteryPercent();
            }
            case WidgetProgress.MUSIC:{
                return getMusicDurationPercent();
            }
            case WidgetProgress.MONTH:{
                return getCurrentMonthPercent();
            }
            case WidgetProgress.WEEK:{
                return getCurrentWeekPercent();
            }
            case WidgetProgress.DAY:{
                return getCurrentDayPercent();
            }
            case WidgetProgress.HOUR:{
                return getCurrentHourPercent();
            }
            default:
            return 0;
        }
    }

    /**
     * 获取手机电量的百分比
     *
     * @return
     */
    public static float getSystemBatteryPercent() {
        return CustomPlugUtil.getBatteryLevel()*1.0f / 100;
    }

    /**
     * 获取音乐时长
     *
     * @return
     */
    public static float getMusicDurationPercent() {
        long totalDuration = CustomPlugUtil.duration;
        long currentDuration = CustomPlugUtil.currentDuration;
        if (totalDuration == 0) {
            return 0;
        } else {
            return currentDuration*1.0f / totalDuration;
        }
    }
    /**
     * 获取当月的进度
     *
     * @return
     */
    public static float getCurrentMonthPercent() {
        long start = TimeUtils.getTimesMonthmorning().getTime();
        long end = TimeUtils.getTimesMonthnight().getTime();
        Log.i("test_start:",start+"");
        Log.i("test_start1:",end+"");
        Log.i("test_start2:",TimeUtils.getCurrentTimeStamp()+"");
        return (TimeUtils.getCurrentTimeStamp()*1000-start)*1.0f / (end -start);
    }

    /**
     * 获取当周的进度
     *
     * @return
     */
    public static float getCurrentWeekPercent() {
        long start = TimeUtils.getTimesWeekmorning().getTime();
        long end = TimeUtils.getTimesWeeknight().getTime();
        return (TimeUtils.getCurrentTimeStamp()*1000-start)*1.0f / (end -start);
    }

    /**
     * 获取当天的进度
     *
     * @return
     */
    public static float getCurrentDayPercent() {
        long start = TimeUtils.getTimesmorning().getTime();
        long end = TimeUtils.getTimesnight().getTime();
        return (TimeUtils.getCurrentTimeStamp()*1000-start)*1.0f / (end -start);
    }

    /**
     * 获取小时的进度
     *
     * @return
     */
    public static float getCurrentHourPercent() {
        float current = TimeUtils.getCurrentMinute();
        return current*1.0f / 60;
    }

}
