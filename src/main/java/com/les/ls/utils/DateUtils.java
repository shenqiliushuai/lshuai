package com.les.ls.utils;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author lshuai
 */
public class DateUtils {

    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";

    public static void main(String[] args) {
        System.out.println(stringToDate("2022-01-15 17:53:10", yyyy_MM_dd_HH_mm_ss));
        System.out.println(dateToString(new Date(), yyyy_MM_dd_HH_mm_ss));
    }

    /**
     * 计算时间间隔
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间间隔（毫秒）
     */
    public static long timeInterval(Date start, Date end) {
        return timeInterval(start.toInstant(), end.toInstant());
    }

    /**
     * 计算时间间隔
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间间隔（毫秒）
     */
    public static long timeInterval(Instant start, Instant end) {
        return Duration.between(start, end).toMillis();
    }

    /**
     * 字符串转时间
     *
     * @param dateStr   时间字符串
     * @param formatStr 字符串日期格式
     * @return 时间
     */
    public static Date stringToDate(String dateStr, String formatStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatStr);
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, dateTimeFormatter);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 时间转字符串
     *
     * @param date      时间对象
     * @param formatStr 字符串日期格式
     * @return 时间字符串
     */
    public static String dateToString(Date date, String formatStr) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(formatStr);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        return localDateTime.format(dateTimeFormatter);
    }

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
        DateTimeFormatter ftf = DateTimeFormatter.ofPattern(yyyy_MM_dd_HH_mm_ss);
        LocalDateTime parse = LocalDateTime.parse(stringBuilder.append(" 00:00:00").toString(), ftf);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
    }

    /**
     * 时间戳返回字符串
     *
     * @param time   long类型的time（单位：秒）
     * @param format 格式
     * @return 格式化后的字符串（2019-01-01 00:00:00）
     */
    public static String getFormatDate(Long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
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