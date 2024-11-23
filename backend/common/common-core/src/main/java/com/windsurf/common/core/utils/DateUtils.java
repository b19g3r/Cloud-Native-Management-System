package com.windsurf.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.ParseException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * Date utility class using Java 8+ date/time API
 */
@Slf4j
public class DateUtils {

    public static final String YYYY = "yyyy";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String HH_MM_SS = "HH:mm:ss";

    private static final String[] PARSE_PATTERNS = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
            "yyyyMMdd", "yyyyMMddHHmmss", "yyyyMMddHHmm", "yyyyMM" };

    /**
     * Get current date in yyyy-MM-dd format
     */
    public static String getDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD));
    }

    /**
     * Get current date and time in yyyy-MM-dd HH:mm:ss format
     */
    public static String getDateTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS));
    }

    /**
     * Get current date and time in specified format
     */
    public static String dateTimeNow(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * Get server start time
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * Calculate days between two dates
     */
    public static int daysBetween(LocalDate date1, LocalDate date2) {
        return (int) ChronoUnit.DAYS.between(date1, date2);
    }

    /**
     * Calculate months between two dates
     */
    public static int monthsBetween(LocalDate date1, LocalDate date2) {
        return (int) ChronoUnit.MONTHS.between(date1, date2);
    }

    /**
     * Calculate years between two dates
     */
    public static int yearsBetween(LocalDate date1, LocalDate date2) {
        return (int) ChronoUnit.YEARS.between(date1, date2);
    }

    /**
     * Calculate time difference in milliseconds
     */
    public static long millisBetween(LocalDateTime date1, LocalDateTime date2) {
        return Duration.between(date1, date2).toMillis();
    }

    /**
     * Get first day of current month
     */
    public static LocalDate firstDayOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * Get last day of current month
     */
    public static LocalDate lastDayOfMonth() {
        return LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * Get start of day
     * Example: 2020-12-12 00:00:00
     */
    public static LocalDateTime beginOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    /**
     * Get end of day
     * Example: 2020-12-12 23:59:59.999999999
     */
    public static LocalDateTime endOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MAX);
    }

    /**
     * Convert Date to LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Convert LocalDateTime to Date
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Parse date string to Date object
     */
    public static Date parseDate(String str) {
        if (str == null) {
            return null;
        }
        try {
            return org.apache.commons.lang3.time.DateUtils.parseDate(str, PARSE_PATTERNS);
        } catch (ParseException e) {
            log.error("Parse date error: ", e);
            return null;
        }
    }

    /**
     * Get current timestamp in milliseconds
     */
    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    /**
     * Get current timestamp in seconds
     */
    public static long currentTimeSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Check if current time is within range
     */
    public static boolean isInRange(LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }

    /**
     * Format Date to string
     */
    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * Add days to date
     */
    public static LocalDateTime plusDays(LocalDateTime date, long days) {
        return date.plusDays(days);
    }

    /**
     * Subtract days from date
     */
    public static LocalDateTime minusDays(LocalDateTime date, long days) {
        return date.minusDays(days);
    }

    /**
     * Check if date is weekend
     */
    public static boolean isWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    /**
     * Get quarter of year
     */
    public static int getQuarter(LocalDate date) {
        return (date.getMonthValue() - 1) / 3 + 1;
    }
}