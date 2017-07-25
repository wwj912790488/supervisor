package com.arcsoft.supervisor.transcoder.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeHelper {
    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static long MILLISECONDSPERSECOND = 1000;
    public final static long MILLISECONDSPERMINUTE = 60 * MILLISECONDSPERSECOND;
    public final static long MILLISECONDSPERHOUR = 60 * MILLISECONDSPERMINUTE;
    public final static long MILLISECONDSPERDAY = 24 * MILLISECONDSPERHOUR;

    public static String format(Date d) {
        if (d == null)
            return "";
        return new SimpleDateFormat(DATE_FORMAT).format(d);
    }

    public static String format(Date d, String pattern) {
        if (d == null)
            return "";
        return new SimpleDateFormat(pattern).format(d);
    }

    public static String formatYMD(Date d) {
        if (d == null)
            return "";
        return new SimpleDateFormat("yyyy-MM-dd").format(d);
    }

    public final static Date getNow() {
        return new Date();
    }

    public final static Date toYMD(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DATE);

        calendar.clear();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTime();
    }

    public final static int getDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public final static int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public final static Date addTimeSpan(Date date, long timeSpanInMillis) {
        long milliseconds = date.getTime();
        milliseconds += timeSpanInMillis;

        return new Date(milliseconds);
    }

    public final static long getTimeSpan(Date date1, Date date2) {
        long milliseconds1 = date1.getTime();
        long milliseconds2 = date2.getTime();

        return milliseconds1 - milliseconds2;
    }

    public final static Date addMonths(Date date, Integer months) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);

        return calendar.getTime();
    }

    public final static Date addDays(Date date, Integer days) {
        long milliseconds = date.getTime();
        milliseconds += days * MILLISECONDSPERDAY;

        return new Date(milliseconds);
    }

    public final static Date addHours(Date date, Integer hours) {
        long milliseconds = date.getTime();
        milliseconds += hours * MILLISECONDSPERHOUR;

        return new Date(milliseconds);
    }

    public final static Date addMinutes(Date date, Integer miutes) {
        long milliseconds = date.getTime();
        milliseconds += miutes * MILLISECONDSPERMINUTE;

        return new Date(milliseconds);
    }

    public final static Date addSeconds(Date date, Integer seconds) {
        long milliseconds = date.getTime();
        milliseconds += seconds * MILLISECONDSPERSECOND;

        return new Date(milliseconds);
    }

    private final static Integer[] weekDayMasks = new Integer[]{0, 1, 2, 4, 8, 16, 32, 64};

    public final static Integer getWeekDayMasks(Boolean sunday, Boolean monday, Boolean tuesday, Boolean wednesday, Boolean thursday, Boolean friday, Boolean saturday) {
        Integer mask = weekDayMasks[0];

        if (sunday) mask += weekDayMasks[Calendar.SUNDAY];
        if (monday) mask += weekDayMasks[Calendar.MONDAY];
        if (tuesday) mask += weekDayMasks[Calendar.TUESDAY];
        if (wednesday) mask += weekDayMasks[Calendar.WEDNESDAY];
        if (thursday) mask += weekDayMasks[Calendar.THURSDAY];
        if (friday) mask += weekDayMasks[Calendar.FRIDAY];
        if (saturday) mask += weekDayMasks[Calendar.SATURDAY];

        return mask;
    }

    public final static Boolean isWeekDayMasked(Date date, int masks) {
        int dayOfWeek = DateTimeHelper.getDayOfWeek(date);

        return ((weekDayMasks[dayOfWeek] & masks) != 0);
    }

    public final static Date firstDayOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = 1;

        calendar.clear();
        calendar.set(year, month, day, 0, 0, 0);
        return calendar.getTime();
    }

    public final static int daysInMonth(Date date) {
        Date firstDayOfMonth = firstDayOfMonth(date);
        Date firstDayOfNextMonth = addMonths(firstDayOfMonth, 1);

        long timeSpan = getTimeSpan(firstDayOfNextMonth, firstDayOfMonth);
        return (int) (timeSpan / MILLISECONDSPERDAY);
    }

    public final static Date getDate(Date firstDayOfMonth, int dayOfMonth) {
        int daysInMonth = daysInMonth(firstDayOfMonth);

        return addDays(firstDayOfMonth, daysInMonth >= dayOfMonth ? dayOfMonth - 1 : daysInMonth - 1);
    }

    public final static Date firstCalendarDayOfMonth(Date date) {
        Date firstDayOfMonth = firstDayOfMonth(date);
        int dayOfWeek = getDayOfWeek(firstDayOfMonth);

        Date firstCalendarDayOfMonth = addDays(firstDayOfMonth, 1 - dayOfWeek);
        return firstCalendarDayOfMonth;
    }

    public final static Date lastCalendarDayOfMonth(Date date) {
        Date firstDayOfMonth = firstDayOfMonth(date);
        Date firstDayOfNextMonth = addMonths(firstDayOfMonth, 1);

        Date lastDayOfMonth = addDays(firstDayOfNextMonth, -1);
        int dayOfWeek = getDayOfWeek(lastDayOfMonth);

        Date lastCalendarDayOfMonth = addDays(lastDayOfMonth, 8 - dayOfWeek);
        return lastCalendarDayOfMonth;
    }

    /**
     * @param t second (1000*ms)
     * @return hh:mm:ss or hhh...:mm:ss
     */
    public static String formatDuration(long t) {
        StringBuilder buf = new StringBuilder(8);
        int h = (int) t / 3600;
        int hr = (int) t % 3600;
        int m = hr / 60;
        int mr = hr % 60;

        if (h < 10) buf.append('0');
        buf.append(h).append(':');

        if (m < 10) buf.append('0');
        buf.append(m).append(':');

        if (mr < 10) buf.append('0');
        buf.append(mr);

        return buf.toString();
    }

    /**
     * @param hmsm hh:mm:ss:mmm
     * @return Long millisecond
     */
    public static Long hhmmssmmm2Millisecond(String hmsm) {
        if (hmsm == null) return Long.valueOf(-1);

        String[] timeArr = hmsm.split(":");
        if (timeArr == null || timeArr.length < 4) return Long.valueOf(-1);

        Long hour = Long.parseLong(timeArr[0]);
        Long minute = Long.parseLong(timeArr[1]);
        Long second = Long.parseLong(timeArr[2]);
        Long millisecond = Long.parseLong(timeArr[3]);

        Long ms = (hour * 3600 + minute * 60 + second) * 1000 + millisecond;
        return ms;
    }

    public static String millisecond2hhmmssmmm(Long ms) {
        if (ms == null) return null;

        Long hour = ms / 3600000;
        Long remain = ms % 3600000;
        Long minute = remain / 60000;
        remain = remain % 60000;
        Long second = remain / 1000;
        Long millisecond = remain % 1000;

        String hmsm = String.format("%d:%d:%d:%d", hour, minute, second, millisecond);
        return hmsm;
    }
}
