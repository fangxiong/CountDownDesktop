package com.fax.showdt.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateUtils {

    /**
     * 相隔多少天
     *
     * @param target
     * @return
     * @throws ParseException
     */
    public static String getSeparatedTimeForDays(long target) throws ParseException {
        Date targetDate = new Date(target);
        Log.i("test_diff_time:","target time:"+target+" current time:"+TimeUtils.currentTimeMillis());
        Date nowDate = new Date(TimeUtils.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long separatTime = df.parse(df.format(targetDate)).getTime() - df.parse(df.format(nowDate)).getTime();
        int gap = (int) ((separatTime) / (24 * 60 * 60 * 1000));
        if (gap == 0) {
            gap = 1;
        } else if (gap < 0) {
            gap = Math.abs(gap) + 1;
        } else {
            gap = gap + 1;
        }

        return gap + "";

    }

    /**
     * 相差多少天
     *
     * @param targetTime
     * @return
     */
    public static String getDiffTimeForDay(long targetTime) {
        final long now = TimeUtils.currentTimeMillis();
        final long l = Math.abs(now - targetTime);
        long day = MillisTime.toDays(l);
        return day + "";
    }

    /**
     * 获取当天凌晨十二点的时间戳
     * @return
     * @throws ParseException
     */
    public static long getCurrentTimeWithNoHour() throws ParseException{
        Date nowDate = new Date(System.currentTimeMillis());
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(df.format(nowDate)).getTime();
    }

    public static String getDiffTimeForHour(long targetTime) {

        final long now = TimeUtils.currentTimeMillis();
        final long l = Math.abs(now - targetTime);

        long day = MillisTime.toDays(l);
        long hour = MillisTime.toHours(l) - TimeUnit.DAYS.toHours(day);
        return supplementZero(hour);
    }

    public static String getDiffTimeForMinute(long targetTime) {
        final long now =TimeUtils.currentTimeMillis();
        final long l = Math.abs(now - targetTime);

        long day = MillisTime.toDays(l);
        long hour = MillisTime.toHours(l) - TimeUnit.DAYS.toHours(day);
        long min = MillisTime.toMinutes(l) - TimeUnit.DAYS.toMinutes(day) - TimeUnit.HOURS.toMinutes(hour);
        return supplementZero(min);
    }

    public static String getDiffTimeForSecond(long targetTime) {
        final long now = TimeUtils.currentTimeMillis();
        final long l = Math.abs(now - targetTime);

        long day = MillisTime.toDays(l);
        long hour = MillisTime.toHours(l) - TimeUnit.DAYS.toHours(day);
        long min = MillisTime.toMinutes(l) - TimeUnit.DAYS.toMinutes(day) - TimeUnit.HOURS.toMinutes(hour);
        long s = MillisTime.toSeconds(l) - TimeUnit.DAYS.toSeconds(day) - TimeUnit.HOURS.toSeconds(hour)
                - TimeUnit.MINUTES.toSeconds(min);

        return supplementZero(s);
    }

    private static String supplementZero(long time) {
        int gap = 10;
        String zero = "0";
        String result = time + "";
        if (time < gap) {
            result = zero + result;
        }
        return result;
    }


    public static HashMap<String, String> getDiffTimeSplit(long targetTime) {
        final long now = TimeUtils.currentTimeMillis();
        final long l = Math.abs(now - targetTime);
        
        long day = MillisTime.toDays(l);
        long hour = MillisTime.toHours(l) - TimeUnit.DAYS.toHours(day);
        long min = MillisTime.toMinutes(l) - TimeUnit.DAYS.toMinutes(day) - TimeUnit.HOURS.toMinutes(hour);
        long s = MillisTime.toSeconds(l) - TimeUnit.DAYS.toSeconds(day) - TimeUnit.HOURS.toSeconds(hour)
                - TimeUnit.MINUTES.toSeconds(min);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("day", String.valueOf(day));
        hashMap.put("hour", String.valueOf(hour));
        hashMap.put("min", String.valueOf(min));
        hashMap.put("sec", String.valueOf(s));
        return hashMap;
    }

    public static String getFormatStr(String format, long mTargetTime) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(mTargetTime));
    }

    /**
     * 根据当前日期获得是星期几,并输出对应标注
     * time=yyyy-MM-dd
     *
     * @return
     */
    public static String getTagFromWeek(String time) {
        String str = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Constant.LOCALE);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int wek = c.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            str += "日曜日";
        }
        if (wek == 2) {
            str += "月曜日";
        }
        if (wek == 3) {
            str += "火曜日";
        }
        if (wek == 4) {
            str += "水曜日";
        }
        if (wek == 5) {
            str += "木曜日";
        }
        if (wek == 6) {
            str += "金曜日";
        }
        if (wek == 7) {
            str += "土曜日";
        }
        return str;
    }

    /**
     * 根据当前日期获得是星期几,并输出对应标注
     * time=当前时间戳
     *
     * @return
     */
    public static String getWeek(long time) {
        String date = getFormatStr("yyyy-MM-dd", time);
        String str = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Constant.LOCALE);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int wek = c.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            str += "星期日";
        }
        if (wek == 2) {
            str += "星期一";
        }
        if (wek == 3) {
            str += "星期二";
        }
        if (wek == 4) {
            str += "星期三";
        }
        if (wek == 5) {
            str += "星期四";
        }
        if (wek == 6) {
            str += "星期五";
        }
        if (wek == 7) {
            str += "星期六";
        }
        return str;
    }
    /**
     * 获取自定义桌面插件的制作时间格式
     * @param time
     * @return
     */
    public static String getCustomPlugMadeTime(long time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Constant.LOCALE);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        String tmp = simpleDateFormat.format(Long.valueOf(time));
        return tmp;
    }
}
