package com.les.ls.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 日期工具类
 *
 * @author lshuai
 */
public class DateUtils {
    private static final String pattern = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取指定日期中的最小时间(LocalDateTime) 时间戳(s)
     */
    public static long minDateTimeOfDayOfMonth(int year, int month, int day) {
        LocalDateTime minLocalDateTime = LocalDateTime.now().with(LocalDateTime.MIN).withYear(year).withMonth(month).withDayOfMonth(day);
        return LocalDateTime.from(minLocalDateTime).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取时间戳对应日期的最小时间戳 00:00:00
     */
    public static long minDateTimeOfTimestamp(Long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return minDateTimeOfDayOfMonth(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth());
    }

    /**
     * 获取指定日期中的最大时间(LocalDateTime)时间戳(s)
     */
    public static long maxDateTimeOfDayOfMonth(int year, int month, int day) {
        LocalDateTime maxLocalDateTime = LocalDateTime.now().with(LocalDateTime.MAX).withYear(year).withMonth(month).withDayOfMonth(day);
        return LocalDateTime.from(maxLocalDateTime).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 获取时间戳对应的日期的最大时间戳 23:59:59
     */
    public static long maxDateTimeOfTimestamp(Long timestamp) {
        Instant instant = Instant.ofEpochSecond(timestamp);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return maxDateTimeOfDayOfMonth(localDateTime.getYear(), localDateTime.getMonthValue(), localDateTime.getDayOfMonth());
    }

    /**
     * 获取指定时间格式的时间戳
     *
     * @param time 格式 （20190101）
     * @return 时间戳
     */
    public static Long getAppointDate(String time) {
        StringBuilder stringBuilder = new StringBuilder(time);
        stringBuilder.insert(6, "-");
        stringBuilder.insert(4, "-");
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime parse = LocalDateTime.parse(stringBuilder.append(" 00:00:00").toString(), ftf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 时间戳返回字符串
     *
     * @param time long类型的time（单位：秒）
     * @return 格式化后的字符串（2019-01-01 00:00:00）
     */
    public static String getFormatDate(Long time) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(time);
    }

    /**
     * 获取今天23:59:59的时间戳（单位:s）
     */
    public static long getTodayLastTimestamp() {
        return maxDateTimeOfTimestamp(getNewTime());
    }

    /**
     * 获取今天00:00:00的时间戳（单位:s）
     */
    public static long getTodayFirstTimestamp() {
        return minDateTimeOfTimestamp(getNewTime());
    }

    /**
     * 获取当前时间戳（单位:s）
     */
    public static Long getNewTime() {
        return System.currentTimeMillis() / 1000;
    }
}