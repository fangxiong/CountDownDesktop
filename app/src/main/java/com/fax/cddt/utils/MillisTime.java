package com.fax.cddt.utils;


import android.annotation.TargetApi;
import android.os.Build;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MICROSECONDS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 毫秒和各个时间单位之间的抓换类
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public final class MillisTime {

    /**
     * 一秒包含的毫秒数
     */
    public static final long TIME_SECOND = fromSeconds(1);

    /**
     * 一分钟包含的毫秒数
     */
    public static final long TIME_MINUTE = fromMinutes(1);

    /**
     * 一小时包含的毫秒数
     */
    public static final long TIME_HOUR = fromHours(1);

    /**
     * 一天包含的毫秒数
     */
    public static final long TIME_DAY = fromDays(1);

    private MillisTime() {
    }

    /**
     * 把特定单位的时间转化成毫秒
     */
    public static long fromMicros(long time) {
        return from(time, MICROSECONDS);
    }

    public static long fromNanos(long time) {
        return from(time, NANOSECONDS);
    }

    public static long fromSeconds(long time) {
        return from(time, SECONDS);
    }

    public static long fromMinutes(long time) {
        return from(time, MINUTES);
    }

    public static long fromHours(long time) {
        return from(time, HOURS);
    }

    public static long fromDays(long time) {
        return from(time, DAYS);
    }

    public static long from(long time, @NonNull TimeUnit unit) {
        return MILLISECONDS.convert(time, unit);
    }

    /**
     * 把毫秒转化成特定单位的时间
     */
    public static long toMicros(long time) {
        return to(time, MICROSECONDS);
    }

    public static long toNanos(long time) {
        return to(time, NANOSECONDS);
    }

    public static long toSeconds(long time) {
        return to(time, SECONDS);
    }

    public static long toMinutes(long time) {
        return to(time, MINUTES);
    }

    public static long toHours(long time) {
        return to(time, HOURS);
    }

    public static long toDays(long time) {
        return to(time, DAYS);
    }

    public static long to(long time, @NonNull TimeUnit unit) {
        return unit.convert(time, MILLISECONDS);
    }

}
