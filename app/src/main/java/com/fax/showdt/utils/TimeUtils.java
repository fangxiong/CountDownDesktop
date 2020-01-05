package com.fax.showdt.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public final class TimeUtils {

    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSSZ", Constant.LOCALE);

    private static final DateFormat HUMAN_READABLE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ ", Constant.LOCALE);
    private static final DateFormat HUMAN_READABLE_TIME_FILE_NAME_FORMAT = new SimpleDateFormat("yyyy-MM-dd＠HH-mm-ss@SSSZ", Constant.LOCALE);

//    private static final TimeManager TIME_MANAGER = TimeManager.getInstance();

    /**
     * Returns the current time in <b>days<b/> since January 1, 1970 00:00:00.0 UTC.
     */
//    public static long currentTimeDays() {
//        return currentTimeDays(null);
//    }

//    public static long currentTimeDays(TimeZone zone) {
//        return TimeUnit.MILLISECONDS.toDays(currentTimeMillis(zone));
//    }

    /**
     * Returns the current time in <b>seconds<b/> since January 1, 1970 00:00:00.0 UTC.
     */
//    public static long currentTimeSeconds() {
//        return currentTimeSeconds(null);
//    }

//    public static long currentTimeSeconds(@Nullable TimeZone zone) {
//        return TimeUnit.MILLISECONDS.toSeconds(currentTimeMillis(zone));
//    }

    /**
     * Returns the current time in <b>minutes<b/> since January 1, 1970 00:00:00.0 UTC.
     */
//    public static long currentTimeMinutes() {
//        return currentTimeMinutes(null);
//    }

//    public static long currentTimeMinutes(@Nullable TimeZone zone) {
//        return TimeUnit.MILLISECONDS.toMinutes(currentTimeMillis(zone));
//    }

    /**
     * Returns the current time in milliseconds since January 1, 1970 00:00:00.0 UTC.
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }
//
//    public static boolean isSystemTimeCorrect() {
//        return TIME_MANAGER.isSystemTimeCorrect();
//    }

//    public static long currentTimeMillis(@Nullable TimeZone zone) {
//        final long time = currentTimeMillis();
//        return zone == null ? time : time + zone.getRawOffset();
//    }

    /**
     * 开机以来的时间 (秒)
     */
    public static long elapsedRealTimeSeconds() {
        return TimeUnit.MILLISECONDS.toSeconds(SystemClock.elapsedRealtime());
    }

    /**
     * 开机以来的时间 (毫秒)
     */
    public static long elapsedRealTimeMillis() {
        return SystemClock.elapsedRealtime();
    }

    /**
     * 开机以来的时间 (纳秒)
     */
    public static long elapsedRealTimeNanos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return SystemClock.elapsedRealtimeNanos();
        } else {
            return TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime()) + (System.nanoTime() % 1000);
        }
    }

//    public static String createHumanReadableDateTimestampForFileName() {
//        return createHumanReadableDateTimestampForFileName(currentTimeMillis());
//    }

    public static String createHumanReadableDateTimestampForFileName(long time) {
        return createDateTimestamp(time, HUMAN_READABLE_TIME_FILE_NAME_FORMAT);
    }

//    public static String createHumanReadableDateTimestamp() {
//        return createHumanReadableDateTimestamp(currentTimeMillis());
//    }

    public static String createHumanReadableDateTimestamp(long time) {
        return createDateTimestamp(time, HUMAN_READABLE_TIME_FORMAT);
    }

//    public static String createDateTimestamp() {
//        return createDateTimestamp(currentTimeMillis(), TIME_FORMAT);
//    }

    public static String createDateTimestamp(long time) {
        return createDateTimestamp(time, TIME_FORMAT);
    }

    private static String createDateTimestamp(long time, DateFormat format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return format.format(calendar.getTime());
    }

    public static long getCurrentTimeStamp() {
        long time = System.currentTimeMillis() / 1000;
        return time;
    }

    public static String stampToDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(timestamp));
        return sd;
    }

    public static String covertToDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");//这个是你要转成后的时间的格式
        String sd = sdf.format(new Date(timestamp));
        return sd;
    }

    public static String createDateFromPattern(long timestamp, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String sd = sdf.format(new Date(timestamp));
        return sd;
    }

    /**
     * 获取今年第一天的时间
     *
     * @return 今年第一天时间，单位毫秒
     */
    public static long getYearStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int currentYear = calendar.get(Calendar.YEAR);
        calendar.clear();
        calendar.set(Calendar.YEAR, currentYear);
        return calendar.getTimeInMillis();
    }

    /**
     * Android 音乐播放器应用里，读出的音乐时长为 long 类型以毫秒数为单位，例如：将 234736 转化为分钟和秒应为 03:55 （包含四舍五入）
     * * @param duration 音乐时长
     * * @return
     */
    public static String timeParse(long duration) {
        String time = "";
        Log.i("test_time0:", duration + "");

        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((float) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        Log.i("test_time1:", time);
        return time;
    }

    public static int getCurrentMinute(){
        SimpleDateFormat sdf = new SimpleDateFormat("mm");
        String sd = sdf.format(new Date(System.currentTimeMillis()));
        return Integer.valueOf(sd);
    }

    // 获得当天0点时间
    public static Date getTimesmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得当天24点时间
    public static Date getTimesnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得本周一0点时间
    public static Date getTimesWeekmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    // 获得本周日24点时间
    public static Date getTimesWeeknight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekmorning());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    // 获得本月第一天0点时间
    public static Date getTimesMonthmorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // 获得本月最后一天24点时间
    public static Date getTimesMonthnight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONDAY), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 24);
        return cal.getTime();
    }
}
