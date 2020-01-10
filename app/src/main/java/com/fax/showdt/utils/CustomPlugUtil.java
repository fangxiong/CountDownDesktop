package com.fax.showdt.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.BatteryManager;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;

import com.fax.showdt.AppContext;
import com.fax.showdt.R;
import com.fax.showdt.bean.CustomWidgetConfig;
import com.fax.showdt.bean.ProgressPlugBean;
import com.fax.showdt.bean.TextPlugBean;
import com.fax.showdt.manager.location.LocationManager;
import com.fax.showdt.manager.musicPlug.KLWPSongUpdateManager;
import com.fax.showdt.manager.weather.WeatherManager;
import com.fax.showdt.manager.widget.WidgetProgressPercentHandler;
import com.fax.showdt.service.WidgetUpdateService;
import com.fax.showdt.view.sticker.ProgressStickerDrawHelper;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.BATTERY_SERVICE;

/**
 * Created by fax on 19-4-15.
 */

public class CustomPlugUtil {

    public static int year;
    public static int month;
    public static int day;
    public static int hour_24;
    public static int hour_12;
    public static String minute;
    private final static long ADAPT_OLD_VERSION_TIME_POINT = 1560268800000L;
    private static SimpleDateFormat mYearFormat = new SimpleDateFormat("yyyy", Constant.LOCALE);
    private static SimpleDateFormat mMonthFormat = new SimpleDateFormat("MM", Constant.LOCALE);
    private static SimpleDateFormat mDayFormat = new SimpleDateFormat("dd", Constant.LOCALE);
    private static SimpleDateFormat mHour_24Format = new SimpleDateFormat("HH", Constant.LOCALE);
    private static SimpleDateFormat mHour_12Format = new SimpleDateFormat("hh", Constant.LOCALE);
    private static SimpleDateFormat mMinuteFormat = new SimpleDateFormat("mm", Constant.LOCALE);
    public static String lrc = "";
    public static String singerName = "";
    public static String album = "";
    public static String songName = "";
    public static long currentDuration = 0L;
    public static long duration = 0L;


    public static void refreshTime() {
        long currentTime = TimeUtils.currentTimeMillis();
        Date date = new Date(currentTime);
        year = Integer.valueOf(mYearFormat.format(date));
        month = Integer.valueOf(mMonthFormat.format(date));
        day = Integer.valueOf(mDayFormat.format(date));
        hour_24 = Integer.valueOf(mHour_24Format.format(date));
        hour_12 = Integer.valueOf(mHour_12Format.format(date));
        minute = mMinuteFormat.format(date);
    }

    /**
     * 获取中文时间段提示
     *
     * @return 晚上;下午;上午;
     */
    public static String getCertainTimeChinaStr() {
        refreshTime();
        String str = "";
        if ((0 <= hour_24 && hour_24 <= 4) || (19 <= hour_24 && hour_24 <= 23)) {
            str = "晚上";
        } else if (12 <= hour_24 && hour_24 <= 18) {
            str = "下午";
        } else {
            str = "上午";
        }
        return str;
    }

    /**
     * 获取英文时间段提示
     *
     * @return am;pm
     */
    public static String getCertainTimeEnglishStr() {
        refreshTime();
        String str = "";
        if (0 <= hour_24 && hour_24 < 12) {
            str = "am";
        } else {
            str = "pm";
        }
        return str;
    }

    /**
     * 获取电池电量level
     *
     * @return
     */
    public static int getBatteryLevel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BatteryManager batteryManager = (BatteryManager) AppContext.get().getSystemService(BATTERY_SERVICE);
                int level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                if (level == 0) {
                    Intent intent = new ContextWrapper(AppContext.get()).
                            registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100 /
                            intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                }
                return level;
            } else {
                Intent intent = new ContextWrapper(AppContext.get()).
                        registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                return (intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) * 100) /
                        intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getLrc() {
        String result = "";
        if (TextUtils.isEmpty(lrc)) {
            result = KLWPSongUpdateManager.lrcStr;
        } else {
            result = lrc;
        }
        if (TextUtils.isEmpty(result)) {
            result = "歌词";
        }
        return result;
    }

    /**
     * 获取歌手名字
     * @return
     */
    public static String getSingerName() {
        String result = "";
        if (TextUtils.isEmpty(singerName)) {
            result = KLWPSongUpdateManager.singerName;
        } else {
            result = singerName;
        }
        if (TextUtils.isEmpty(result)) {
            result = "歌手名";
        }
        return result;
    }

    /**
     * 获取歌名
     * @return
     */
    public static String getSongName() {
        String result = "";
        if (TextUtils.isEmpty(songName)) {
            result = KLWPSongUpdateManager.songName;
        } else {
            result = songName;
        }
        if (TextUtils.isEmpty(result)) {
            result = "歌名";
        }
        return result;
    }

    public static long getDuration(){
        long result = 0L;
        if (duration == 0L) {
            result = KLWPSongUpdateManager.duration;
        } else {
            result = duration;
        }
        return result;
    }
    public static long getCurrentDuration(){
        long result = 0L;
        if (currentDuration == 0L) {
            result = KLWPSongUpdateManager.currentDuration;
        } else {
            result = currentDuration;
        }
        return result;
    }

    /**
     * 获取专辑名称
     * @return
     */
    public static String getAlbum() {
        String result = "";
        if (TextUtils.isEmpty(album)) {
            result = KLWPSongUpdateManager.album;
        } else {
            result = album;
        }
        if (TextUtils.isEmpty(result)) {
            result = "专辑";
        }
        return result;
    }

    /**
     * 获取时间插件的格式
     *
     * @param timeId
     * @return
     */
    public static String getTimePlugText(String timeId) {
        refreshTime();
        String timeStr = "";
        switch (timeId) {
            case "T00": {
                timeStr = hour_12 + ":" + minute + " " + getCertainTimeEnglishStr();
                break;
            }
            case "T01": {
                timeStr = hour_24 + ":" + minute;
                break;
            }
            case "T02": {
                timeStr = getCertainTimeChinaStr() + hour_12 + ":" + minute;
                break;
            }
            default: {
                break;
            }
        }
        return timeStr;
    }

    /**
     * 获取日期插件的格式
     *
     * @param dateId
     * @return
     */
    public static String getDatePlugText(String dateId) {
        refreshTime();
        String dateStr = "";
        switch (dateId) {
            case "D00": {
                dateStr = month + "/" + day;
                break;
            }
            case "D01": {
                dateStr = month + "." + day;
                break;
            }
            case "D02": {
                dateStr = month + "-" + day;
                break;
            }
            case "D03": {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d", Locale.ENGLISH);
                dateStr = dateFormat.format(new Date(TimeUtils.currentTimeMillis()));
                break;
            }
            case "D04": {
                dateStr = month + "月" + day + "日";
                break;
            }
            default: {
                break;
            }
        }
        return dateStr;
    }

    /**
     * 获取星期插件的格式
     *
     * @param weekId
     * @return
     */
    public static String getWeekPlugText(String weekId) {
        refreshTime();
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(new Date(TimeUtils.currentTimeMillis()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        int wek = c.get(Calendar.DAY_OF_WEEK);
        String weekStr = "";
        switch (wek) {
            case 1: {
                switch (weekId) {
                    case "W00": {
                        weekStr = "Sunday";
                        break;
                    }
                    case "W01": {
                        weekStr = "SUN";
                        break;
                    }
                    case "W02": {
                        weekStr = "周日";
                        break;
                    }
                    case "W03": {
                        weekStr = "星期日";
                        break;
                    }
                    case "W04": {
                        weekStr = "日曜日";
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }
            case 2: {
                switch (weekId) {
                    case "W00": {
                        weekStr = "Monday";
                        break;
                    }
                    case "W01": {
                        weekStr = "MON";
                        break;
                    }
                    case "W02": {
                        weekStr = "周一";
                        break;
                    }
                    case "W03": {
                        weekStr = "星期一";
                        break;
                    }
                    case "W04": {
                        weekStr = "月曜日";
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }
            case 3: {
                switch (weekId) {
                    case "W00": {
                        weekStr = "Tuesday";
                        break;
                    }
                    case "W01": {
                        weekStr = "TUE";
                        break;
                    }
                    case "W02": {
                        weekStr = "周二";
                        break;
                    }
                    case "W03": {
                        weekStr = "星期二";
                        break;
                    }
                    case "W04": {
                        weekStr = "火曜日";
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }
            case 4: {
                switch (weekId) {
                    case "W00": {
                        weekStr = "Wednesday";
                        break;
                    }
                    case "W01": {
                        weekStr = "WED";
                        break;
                    }
                    case "W02": {
                        weekStr = "周三";
                        break;
                    }
                    case "W03": {
                        weekStr = "星期三";
                        break;
                    }
                    case "W04": {
                        weekStr = "水曜日";
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }
            case 5: {
                switch (weekId) {
                    case "W00": {
                        weekStr = "Thursday";
                        break;
                    }
                    case "W01": {
                        weekStr = "THUR";
                        break;
                    }
                    case "W02": {
                        weekStr = "周四";
                        break;
                    }
                    case "W03": {
                        weekStr = "星期四";
                        break;
                    }
                    case "W04": {
                        weekStr = "木曜日";
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }
            case 6: {
                switch (weekId) {
                    case "W00": {
                        weekStr = "Friday";
                        break;
                    }
                    case "W01": {
                        weekStr = "FRI";
                        break;
                    }
                    case "W02": {
                        weekStr = "周五";
                        break;
                    }
                    case "W03": {
                        weekStr = "星期五";
                        break;
                    }
                    case "W04": {
                        weekStr = "金曜日";
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }
            case 7: {
                switch (weekId) {
                    case "W00": {
                        weekStr = "Saturday";
                        break;
                    }
                    case "W01": {
                        weekStr = "SAT";
                        break;
                    }
                    case "W02": {
                        weekStr = "周六";
                        break;
                    }
                    case "W03": {
                        weekStr = "星期六";
                        break;
                    }
                    case "W04": {
                        weekStr = "土曜日";
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }

        }
        return weekStr;
    }

    /**
     * 获取系统电量插件格式
     *
     * @param batteryId
     * @return
     */
    public static String getBatteryPlugText(String batteryId) {
        String progress = "";
        int battery = getBatteryLevel();
        switch (batteryId) {
            case "B00": {
                progress = String.valueOf(battery);
                break;
            }
            case "B01":{
                progress = String.valueOf((int)(WidgetProgressPercentHandler.getMusicDurationPercent()*100));
                break;
            }
            case "B02":{
                progress = String.valueOf((int)(WidgetProgressPercentHandler.getCurrentMonthPercent()*100));
                break;
            }
            case "B03":{
                progress = String.valueOf((int)(WidgetProgressPercentHandler.getCurrentWeekPercent()*100));
                break;
            }
            case "B04":{
                progress = String.valueOf((int)(WidgetProgressPercentHandler.getCurrentDayPercent()*100));
                break;
            }
            case "B05":{
                progress = String.valueOf((int)(WidgetProgressPercentHandler.getCurrentHourPercent()*100));
                break;
            }
            default: {
                break;
            }
        }
        return progress;
    }

    /**
     * 获取歌词插件格式
     *
     * @param lrcId
     * @return
     */
    public static String getLrcPlugText(String lrcId) {
        String result = "";
        switch (lrcId) {
            case "L00": {
                result = getLrc();
                break;
            }
            case "L01": {
                result =getAlbum();
                break;
            }
            case "L02": {
                result = getSingerName();
                break;
            }
            case "L03": {
                result = getSongName();
                break;
            }
            case "L04": {
                result = TimeUtils.timeParse(getDuration());
                break;
            }
            case "L05":{
                result = TimeUtils.timeParse(getCurrentDuration());
                break;
            }
        }
        return result;
    }

    /**
     * 获取天气
     *
     * @param weatherId
     * @return
     */
    public static String getWeather(String weatherId) {
        WeatherManager.getInstance().starGetWeather();
        String result = "";
        switch (weatherId) {
            case "S00": {
                result = LocationManager.LOCATION;
                break;
            }
            case "S01": {
                result = WeatherManager.weather;
                break;
            }
            case "S02": {
                result = WeatherManager.temperature;
                break;
            }
            default: {
                break;
            }
        }
        return result;
    }

    /**
     * 获取倒计时插件格式
     *
     * @param countdownId
     * @return
     */
    public static String getCountdownPlugText(String countdownId) {
        String cdStr = "";
        String separatedDay = "";
        String id = "";
        String d = "";
        String h = "";
        String m = "";
        String s = "";
        long targetTime = 0;
        boolean isDefaultMode = true;
        String[] strs = countdownId.split("-");
        if (TextUtils.isDigitsOnly(strs[1]) && (strs[1].length() == 13 || strs[1].length() == 12)) {
            targetTime = Long.valueOf(strs[1]);
            id = strs[0];
            if (countdownId.charAt(countdownId.length() - 1) == '-') {
                isDefaultMode = false;
            }
            try {
                separatedDay = DateUtils.getSeparatedTimeForDays(targetTime);
                HashMap<String, String> mHashMap0 = DateUtils.getDiffTimeSplit(targetTime);
                d = getTimeCompletion(mHashMap0.get("day"));
                h = getTimeCompletion(mHashMap0.get("hour"));
                m = getTimeCompletion(mHashMap0.get("min"));
                s = getTimeCompletion(mHashMap0.get("sec"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (id) {
                case "C00": {
                    if (isDefaultMode) {
                        cdStr = separatedDay;
                    } else {
                        cdStr = d + "天" + h + "时" + m + "分" + s + "秒";
                    }
                    try {
                        if (targetTime <= DateUtils.getCurrentTimeWithNoHour()) {
                            if (isDefaultMode) {
                                cdStr = "0";
                            } else {
                                cdStr = "0天0时0分0秒";
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case "C01": {
                    if (isDefaultMode) {
                        cdStr = String.valueOf(Math.abs(Integer.valueOf(separatedDay)));
                    } else {
                        cdStr = d + "天" + h + "时" + m + "分" + s + "秒";
                    }
                    break;
                }
                default: {
                    break;
                }
            }
            // DebugLog.i("test_countdownId:", countdownId);
            // DebugLog.i("test_countdownId_str:", cdStr);
            return cdStr;
        }
        return "[" + countdownId + "]";
    }


    public static String getPlugTextFromSign(String sign) {
        if (!TextUtils.isEmpty(sign)) {
            String firstChar = String.valueOf(sign.charAt(0));
            switch (firstChar) {
                case "T": {
                    return getTimePlugText(sign);
                }
                case "D": {
                    return getDatePlugText(sign);
                }
                case "W": {
                    return getWeekPlugText(sign);
                }
                case "B": {
                    return getBatteryPlugText(sign);
                }
                case "C": {
                    return getCountdownPlugText(sign);
                }
                case "L": {
                    return getLrcPlugText(sign);
                }
                case "S": {
                    return getWeather(sign);
                }
                default: {
                    break;
                }
            }
        }
        return "";
    }

    /**
     * 解析含特殊码的字符串
     *
     * @param str
     * @return
     */
    public static String getPlugTextFromSigns(String str) {
        int leftPos = -1;
        int rightPos = -1;
        String finalStr = "";
        String c = "";
        String code = "";
        if (!TextUtils.isEmpty(str) && !str.contains("[") && !str.contains("]")) {
            return str;
        }
        for (int i = 0; i < str.length(); i++) {
            c = String.valueOf(str.charAt(i));
            if (c.equals("[")) {
                leftPos = i;
            }
            if (c.equals("]")) {
                if (leftPos != -1) {
                    code = str.substring(leftPos + 1, i);
                    if (isContainSpecialSign(code)) {
                        if (rightPos == -1) {
                            finalStr = str.substring(0, leftPos) + getPlugTextFromSign(code);
                        } else {
                            finalStr = finalStr + str.substring(rightPos + 1, leftPos) + getPlugTextFromSign(code);
                        }
                    } else {
                        if (rightPos == -1) {
                            finalStr = str.substring(0, i + 1);
                        } else {
                            finalStr = finalStr + str.substring(rightPos + 1, i + 1);
                        }
                    }
                }
                rightPos = i;
            }
        }
        finalStr = finalStr + str.substring(rightPos + 1, str.length());
        return finalStr;
    }

    /**
     * 文本中是否包含插件的标识码
     *
     * @param str
     * @return
     */
    public static boolean isContainSpecialSignFromText(String str) {
        int leftPos = -1;
        String c = "";
        String code = "";
        if (!TextUtils.isEmpty(str) && !str.contains("[") && !str.contains("]")) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            c = String.valueOf(str.charAt(i));
            if (c.equals("[")) {
                leftPos = i;
            }
            if (c.equals("]")) {
                if (leftPos != -1) {
                    code = str.substring(leftPos + 1, i);
                    if (isContainSpecialSign(code)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断截取的特殊码是否为插件的标识码
     *
     * @param code
     * @return
     */
    public static boolean isContainSpecialSign(String code) {
        String[] signs = getSpecialSignArray();
        for (int j = 0; j < signs.length; j++) {
            if (code.length() > 3) {
                String str = code.substring(0, 4);
                if ((str.equals("C00-") || str.equals("C01-"))) {
                    return true;
                }
            }
            if (signs[j].equals(code)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否包含计时器特殊code
     *
     * @param text
     * @return
     */
    public static boolean checkContainTimerCode(String text) {
        String result = "";
        result = StringUtils.substringBetween(text, "[C00-", "-]");
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C00-", "]");
        }
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C01-", "-]");
        }
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C01-", "]");
        }
        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result)) {
            return true;
        }
        return false;

    }

    /**
     * 判断是否是天时分秒的计时格式
     *
     * @param text
     * @return
     */
    public static boolean checkContainSpecialTimerCode(String text) {
        String result = "";
        result = StringUtils.substringBetween(text, "[C00-", "-]");
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C01-", "-]");
        }
        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result)) {
            return true;
        }
        return false;
    }

    /**
     * 计时器是否为默认模式
     * 默认模式：（天） 特殊模式： 天时分秒
     *
     * @param text
     * @return
     */
    public static boolean checkTimerDefaultMode(String text) {
        String result = "";
        result = StringUtils.substringBetween(text, "[C00-", "-]");
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C01-", "-]");
        }
        if (TextUtils.isEmpty(result)) {
            return true;
        }
        return false;
    }

    /**
     * 切换计时器的模式（默认模式和特殊模式）
     *
     * @param text
     * @param isDefaultMode
     * @return
     */
    public static String changeTimerMode(String text, boolean isDefaultMode) {
        String result = "";
        result = StringUtils.substringBetween(text, "[C00-", "]");
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C01-", "]");
        }
        if (!TextUtils.isEmpty(result)) {
            if (TextUtils.isDigitsOnly(result)) {
                if (!isDefaultMode) {
                    return text.replace(result, result + "-");
                }
            } else {
                if (isDefaultMode) {
                    return text.replace(result, result.replace("-", ""));
                }
            }
        }
        return text;

    }

    /**
     * 修改计时器的时间
     *
     * @param text
     * @param time
     * @return
     */
    public static String changeTimerConfig(String text, long time) {
        String result = "";
        result = StringUtils.substringBetween(text, "[C00-", "-]");
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C00-", "]");
        }
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C01-", "-]");
        }
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C01-", "]");
        }

        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result)) {
            return text.replace(result, String.valueOf(time));
        } else {
            return text;
        }

    }


    /**
     * 用户选时间时 判断是正计时还是倒计时
     * 同时要改变code
     *
     * @param text
     * @param time
     * @return
     */
    public static String posAndNegSwitchTimer(String text, long time) {
        String result = text;
        try {
            if (time <= DateUtils.getCurrentTimeWithNoHour()) {
                result = text.replaceFirst("C00-", "C01-");
            } else {
                result = text.replaceFirst("C01-", "C00-");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取计时器的设定时间
     *
     * @param text
     * @return
     */
    public static long getTimerTargetTime(String text) {
        String result = "";
        result = StringUtils.substringBetween(text, "[C00-", "-]");
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C00-", "]");
        }
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C01-", "-]");
        }
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C01-", "]");
        }

        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result)) {
            return Long.valueOf(result);
        } else {
            return 0;
        }
    }

    public static boolean isContainMusicPlug(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return StringUtils.contains(text, "[L0");
    }

    /**
     * 是否包含天时分秒的倒计时插件
     *
     * @param text
     * @return
     */
    public static boolean isContainSpecialTimerPlug(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        return getSpecialTimerPlugTargetTime(text) != 0;
    }

    /**
     * 是否包含天时分秒的倒计时插件
     *
     * @param text
     * @return
     */
    public static boolean isOnlySpecialTimerPlug(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        if (StringUtils.startsWith(text, "[C00-") || StringUtils.startsWith(text, "[C01-")) {
            if (StringUtils.endsWith(text, "]")) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 获取文本中的天时分秒倒计时元素的目标时间
     *
     * @param text
     * @return
     */
    public static long getSpecialTimerPlugTargetTime(String text) {
        String result = "";
        result = StringUtils.substringBetween(text, "[C00-", "-]");
        if (TextUtils.isEmpty(result)) {
            result = StringUtils.substringBetween(text, "[C01-", "-]");
        }
        if (!TextUtils.isEmpty(result) && TextUtils.isDigitsOnly(result)) {
            return Long.valueOf(result);
        } else {
            return 0;
        }
    }

    public static String[] getSpecialSignArray() {
        Resources res = AppContext.get().getResources();
        return res.getStringArray(R.array.custom_plug_special_sign);
    }

    public static SpannableStringBuilder getSpannableStrFromSigns(String str, Context mContext) {

        int leftPos = -1;
        int rightPos = -1;
        SpannableStringBuilder finalStr = SpannableStringUtils.getBuilder("").create();
        for (int i = 0; i < str.length(); i++) {
            String c = String.valueOf(str.charAt(i));
            if (c.equals("[")) {
                leftPos = i;
            }
            if (c.equals("]")) {
                if (leftPos != -1) {
                    String code = str.substring(leftPos + 1, i);
                    if (isContainSpecialSign(code)) {
                        if (rightPos == -1) {
                            finalStr.append(str.substring(0, leftPos))
                                    .append(SpannableStringUtils.getBuilder("").append("[" + code + "]").setBitmap(getBitmapFromText(getPlugTextFromSign(code), mContext)).create());
                        } else {
                            finalStr.append(str.substring(rightPos + 1, leftPos))
                                    .append(SpannableStringUtils.getBuilder("").append("[" + code + "]").setBitmap(getBitmapFromText(getPlugTextFromSign(code), mContext)).create());
                        }
                    } else {
                        if (rightPos == -1) {
                            finalStr.append(str.substring(0, i + 1));
                        } else {
                            finalStr.append(str.substring(rightPos + 1, i + 1));
                        }
                    }
                }
                rightPos = i;
            }
        }
        finalStr.append(str.substring(rightPos + 1, str.length()));
        return finalStr;
    }

    public static Bitmap getBitmapFromText(String code, Context context) {
        String str = "[" + code + "]";
        Paint paint = new Paint();
        paint.setTextSize(ViewUtils.sp2px(16,context));
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setColor(context.getResources().getColor(R.color.c_FCF43C));
        paint.setAntiAlias(true);

        Paint.FontMetricsInt fm = paint.getFontMetricsInt();

        int width = (int) paint.measureText(str);
        int height = fm.descent - fm.ascent;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(str, 0, fm.leading - fm.ascent, paint);
        canvas.save();
        return bitmap;
    }

    public static String getTimeCompletion(String x) {
        if (x != null && x.length() == 1) {
            return "0" + x;
        } else {
            return x;
        }
    }



    public static @WidgetUpdateService.RefreshGap int getWidgetRefreshGap(CustomWidgetConfig config){
        if(config == null){
            return WidgetUpdateService.REFRESH_WITH_ONE_SECOND;
        }
        List<ProgressPlugBean> progressPlugBeanList= config.getProgressPlugList();
        List<TextPlugBean> textPlugBeanList= config.getTextPlugList();
        if(progressPlugBeanList.isEmpty()&&textPlugBeanList.isEmpty()){
            return WidgetUpdateService.REFRESH_WITH_JUST_ONCE;
        }else{
            if(!textPlugBeanList.isEmpty()){
                for(TextPlugBean bean:textPlugBeanList){
                    //包含歌词插件和天时分秒的倒计时插件则按每秒更新
                    if(isContainMusicPlug(bean.getText()) || checkContainSpecialTimerCode(bean.getText())){
                        return WidgetUpdateService.REFRESH_WITH_ONE_SECOND;
                    }
                }
            }
            return WidgetUpdateService.REFRESH_WITH_SIXTY_SECOND;
        }
    }

}


