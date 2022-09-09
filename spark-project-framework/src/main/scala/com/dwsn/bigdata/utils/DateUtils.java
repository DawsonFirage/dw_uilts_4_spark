package com.dwsn.bigdata.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : Abo
 * @date : 2022/1/1 14:04
 */
public class DateUtils {

    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * Date类型格式化成String类型
     *
     * @return String类型日期
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * String类型格式化指定格式的Date类型
     *
     * @return 格式化后的Date类型
     */
    public static Date parse(String date, String format) throws ParseException {
        return new SimpleDateFormat(format).parse(date);
    }

    /**
     * 比较两个Date类型是否相等
     *
     * @return boolean
     */
    public static boolean sameDay(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return true;
        }
        if (date1 == null || date2 == null) {
            return false;
        }
        String str1 = formatDate(date1, "yyyy-MM-dd");
        String str2 = formatDate(date2, "yyyy-MM-dd");
        return StringUtils.equals(str1, str2);
    }

    /**
     * 获取当前时间
     */
    public static Date currentTime() {
        return new Date();
    }

    /**
     * 获取当前时间几天前的时间
     *
     * @param days 表示几天前
     * @return Date
     */
    public static Date getBeforeByDays(Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - days);
        return calendar.getTime();
    }

    /**
     * 获取指定时间几天前的时间
     *
     * @param date 指定时间
     * @param days 表示几天前
     * @return Date
     */
    public static Date getBeforeByDays(Date date, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - days);
        return calendar.getTime();
    }

    /**
     * 获取当前时间几小时前的时间
     *
     * @param hours 表示几小时前
     * @return Date
     */
    public static Date getBeforeByHours(Integer hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hours);
        return calendar.getTime();
    }

    /**
     * 获取指定时间几小时前的时间
     *
     * @param date  指定时间
     * @param hours 表示几小时前
     * @return Date
     */
    public static Date getBeforeByHours(Date date, Integer hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hours);
        return calendar.getTime();
    }

    /**
     * 获取当前时间几分钟前的时间
     *
     * @param minutes 表示几分钟前
     * @return Date
     */
    public static Date getBeforeByMinutes(Integer minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - minutes);
        return calendar.getTime();
    }

    /**
     * 获取指定时间几分钟前的时间
     *
     * @param date    指定时间
     * @param minutes 表示几分钟前
     * @return Date
     */
    public static Date getBeforeByMinutes(Date date, Integer minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) - minutes);
        return calendar.getTime();
    }

    /**
     * 获取当前时间几秒前的时间
     *
     * @param seconds 表示几秒前
     * @return Date
     */
    public static Date getBeforeBySeconds(Integer seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - seconds);
        return calendar.getTime();
    }

    /**
     * 获取指定时间几秒前的时间
     *
     * @param date    指定时间
     * @param seconds 表示几秒前
     * @return Date
     */
    public static Date getBeforeBySeconds(Date date, Integer seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) - seconds);
        return calendar.getTime();
    }

    /**
     * 获取当前时间几天后的时间
     *
     * @param days 表示几天后
     * @return Date
     */
    public static Date getAfterByDays(Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + days);
        return calendar.getTime();
    }

    /**
     * 获取指定时间几天后的时间
     *
     * @param date 指定时间
     * @param days 表示几天后
     * @return Date
     */
    public static Date getAfterByDays(Date date, Integer days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + days);
        return calendar.getTime();
    }

    /**
     * 获取当前时间几小时后的时间
     *
     * @param hours 表示几小时后
     * @return Date
     */
    public static Date getAfterByHours(Integer hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hours);
        return calendar.getTime();
    }

    /**
     * 获取指定时间几小时后的时间
     *
     * @param date  指定时间
     * @param hours 表示几小时后
     * @return Date
     */
    public static Date getAfterByHours(Date date, Integer hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hours);
        return calendar.getTime();
    }

    /**
     * 获取当前时间几分钟后的时间
     *
     * @param minutes 表示几分钟后
     * @return Date
     */
    public static Date getAfterByMinutes(Integer minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + minutes);
        return calendar.getTime();
    }

    /**
     * 获取指定时间几分钟后的时间
     *
     * @param date    指定时间
     * @param minutes 表示几分钟后
     * @return Date
     */
    public static Date getAfterByMinutes(Date date, Integer minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + minutes);
        return calendar.getTime();
    }

    /**
     * 获取当前时间几秒后的时间
     *
     * @param seconds 表示几秒后
     * @return Date
     */
    public static Date getAfterBySeconds(Integer seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + seconds);
        return calendar.getTime();
    }

    /**
     * 获取指定时间几秒后的时间
     *
     * @param date    指定时间
     * @param seconds 表示几秒后
     * @return Date
     */
    public static Date getAfterBySeconds(Date date, Integer seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, calendar.get(Calendar.SECOND) + seconds);
        return calendar.getTime();
    }

    /**
     * 加减日期的年、月、日、时、分、秒
     *
     * @param date  日期
     * @param field 处理的字段，建议使用Calendar的常量：
     *              [Calendar.YEAR]
     *              [Calendar.MONTH]
     *              [Calendar.DAY_OF_YEAR]
     *              [Calendar.HOUR]
     *              [Calendar.MINUTE]
     *              [Calendar.SECOND]
     * @param value 加减值，负数为减
     */
    public static Date handleTime(Date date, int field, int value) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, value);
        return calendar.getTime();
    }

    /**
     * 计算两个日期相差秒数
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 相差秒数
     */
    public static long differSeconds(Date startDate, Date endDate) {
        long diffTime = endDate.getTime() - startDate.getTime();
        return (diffTime / 1000);
    }

    /**
     * 计算两个日期相差分钟数
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 相差分钟数
     */
    public static long differMinutes(Date startDate, Date endDate) {
        return differSeconds(startDate, endDate) / 60;
    }

    /**
     * 计算两个日期相差小时数
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 相差小时数
     **/
    public static long differHours(Date startDate, Date endDate) {
        return differMinutes(startDate, endDate) / 60;
    }

    /**
     * 计算两个日期相差天数
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 相差天数
     */
    public static long differDays(Date startDate, Date endDate) {
        return differHours(startDate, endDate) / 24;
    }

    /**
     * 获取一天的开始时间
     *
     * @param date 日期
     * @return 一天的开始时间, 即00:00:00:000
     */
    public static Date getStartOfThisDay(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        setStartTime(cal);
        return cal.getTime();
    }

    /**
     * 设置每日开始时间
     *
     * @param cal 日期
     */
    private static void setStartTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 获取一天的结束时间
     *
     * @param date 传入日期
     * @return 一天的结束时间, 即23:59:59:999
     */
    public static Date getEndOfThisDay(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        setEndTime(cal);
        return cal.getTime();
    }

    /**
     * 设置每日结束时间
     *
     * @param cal 日期
     */
    private static void setEndTime(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
    }

    /**
     * 获取指定日期的年份
     *
     * @param date 日期
     * @return 指定日期的年份
     */
    public static int getYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    /**
     * 获取指定日期的年份
     *
     * @param date 日期
     * @return 指定日期的年份
     */
    public static String getYearStr(Date date) {
        return formatDate(date, "yyyy");
    }

    /**
     * 获取指定日期的月份
     *
     * @param date 日期
     * @return 指定日期的月份
     */
    public static int getMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取指定日期的月份
     *
     * @param date 日期
     * @return 指定日期的月份
     */
    public static String getMonthStr(Date date) {
        return formatDate(date, "MM");
    }

    /**
     * 获取指定日期的日
     *
     * @param date 日期
     * @return 指定日期的日
     */
    public static int getDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定日期的日
     *
     * @param date 日期
     * @return 指定日期的日
     */
    public static String getDayStr(Date date) {
        return formatDate(date, "dd");
    }

    /**
     * 获取指定日期的小时数
     *
     * @param date 日期
     * @return 指定日期的小时数
     */
    public static int getHour(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取指定日期的小时数
     *
     * @param date 日期
     * @return 指定日期的小时数
     */
    public static String getHourStr(Date date) {
        return formatDate(date, "HH");
    }

    /**
     * 获取指定日期的分钟数
     *
     * @param date 日期
     * @return 指定日期的分钟数
     */
    public static int getMinute(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }

    /**
     * 获取指定日期的分钟数
     *
     * @param date 日期
     * @return 指定日期的分钟数
     */
    public static String getMinuteStr(Date date) {
        return formatDate(date, "mm");
    }

    /**
     * 获取指定日期的秒数
     *
     * @param date 日期
     * @return 指定日期的秒数
     */
    public static int getSecond(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.SECOND);
    }

    /**
     * 获取指定日期的秒数
     *
     * @param date 日期
     * @return 指定日期的秒数
     */
    public static String getSecondStr(Date date) {
        return formatDate(date, "ss");
    }

    /**
     * 获取指定日期的是周几
     *
     * @param date 日期
     * @return 周几
     */
    public static int getWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return week == 0 ? 7 : week;
    }

    /**
     * 获取指定日期的是周几
     *
     * @param date 日期
     * @return 周几
     */
    public static String getWeekStr(Date date) {
        return formatDate(date, "E");
    }

    /**
     * 将date转换成LocalDate
     *
     * @param date 日期
     * @return LocalDate
     */
    public static LocalDate convertDate2LocalDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * 将date转换成LocalDate
     *
     * @param date 日期
     * @return LocalDate
     */
    public static LocalDateTime convertDate2LocalDateTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * 将LocalDate转换成Date
     *
     * @param localDate localDate
     * @return date
     */
    public static Date convertLocalDate2Date(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    /**
     * 获取时间范围
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @param format    格式化样式，目前支持：
     *                  yyyyMMdd
     *                  yyyy-MM-dd
     *                  yyyyMMddHHmmss
     *                  yyyy-MM-dd-HH-mm-ss
     * @return 开始时间 - 结束时间
     */
    public static String getRangeDateTime(Date startDate, Date endDate, String format) {
        if (Objects.isNull(startDate)
                || Objects.isNull(endDate)
                || (!StringUtils.equals(format, YYYYMMDD)
                && !StringUtils.equals(format, YYYY_MM_DD)
                && !StringUtils.equals(format, YYYYMMDDHHMMSS)
                && !StringUtils.equals(format, YYYY_MM_DD_HH_MM_SS))) {
            return null;
        }
        return String.format("%s - %s", formatDate(startDate, format), formatDate(endDate, format));
    }


}

