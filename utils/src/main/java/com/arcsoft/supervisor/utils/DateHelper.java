package com.arcsoft.supervisor.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * This is a helper class for date time relation operations.
 * 
 * @author fjli
 */
public class DateHelper {

	public static final int ONE_DAY = 86400000;
	public static final int ONE_MINUTE = 60000;
	public static final int ONE_HOUR = 3600000;
	public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	/**
	 * Check the specified year is leap year or not.
	 * 
	 * @param year - the specified year to be checked.
	 * @return Returns true if the specified year is leap year, otherwise returns false.
	 */
	public static boolean isLeapYear(int year) {
		return ((year % 4) == 0 && (year % 100) != 0) || ((year % 400) == 0);
	}

	/**
	 * Create new date without time part.
	 * 
	 * @param year - the year of new date.
	 * @param month - the month of new date.
	 * @param day - the day of new date.
	 */
	public static Date createDate(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month - 1, day);
		return c.getTime();
	}

	/**
	 * Create new date with time part.
	 * 
	 * @param year - the year of new date.
	 * @param month - the month of new date.
	 * @param day - the day of new date.
	 * @param hour - the hour of new date.
	 * @param min - the minute of new date.
	 * @param sec - the seconds of new date.
	 */
	public static Date createDate(int year, int month, int day, int hour, int min, int sec) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.set(year, month - 1, day, hour, min, sec);
		return c.getTime();
	}

	/**
	 * Remove the time part.
	 * 
	 * @param date - The date may include time part.
	 * @return Returns a new date without time part.
	 */
	public static Date trimTime(Date date) {
		int zoneOffset = TimeZone.getDefault().getOffset(date.getTime());
		long datetime = date.getTime();
		long time = (datetime + zoneOffset) % ONE_DAY;
		return (time == 0) ? date : new Date(datetime - time);
	}

	/**
	 * Calculate the days between two dates.
	 * 
	 * <pre>
	 * if (days == 0), indicate date1 == date2
	 * if (days <  0), indicate date1 <  date2
	 * if (days >  0), indicate date1 >  date2
	 * </pre>
	 * 
	 * @param date1 - The first date.
	 * @param date2 - The second date.
	 * @return Returns the days between two dates.
	 */
	public static int dateDiff(Date date1, Date date2) {
		int zoneOffset1 = TimeZone.getDefault().getOffset(date1.getTime());
		int zoneOffset2 = TimeZone.getDefault().getOffset(date2.getTime());
		long time1 = (date1.getTime() + zoneOffset1) / ONE_DAY;
		long time2 = (date2.getTime() + zoneOffset2) / ONE_DAY;
		return (int) (time1 - time2);
	}

	public static int hourDiff(Date date1, Date date2) {
		int zoneOffset1 = TimeZone.getDefault().getOffset(date1.getTime());
		int zoneOffset2 = TimeZone.getDefault().getOffset(date2.getTime());
		long time1 = (date1.getTime() + zoneOffset1) / ONE_HOUR;
		long time2 = (date2.getTime() + zoneOffset2) / ONE_HOUR;
		return (int) (time1 - time2);
	}



	/**
	 * Format time.
	 * 
	 * @param time - The time object to be formatted.
	 * @return Returns the formatted time, if time is null, returns empty string. The time format is "HH:mm:ss".
	 */
	public static String formatTime(Date time) {
		return formatDateTime(time, DEFAULT_TIME_PATTERN);
	}

	/**
	 * Format date.
	 * 
	 * @param date - The date object to be formatted.
	 * @return Returns the formatted date, if date is null, returns empty string. The date format is "yyyy-MM-dd".
	 */
	public static String formatDate(Date date) {
		return formatDateTime(date, DEFAULT_DATE_PATTERN);
	}

	/**
	 * Format date time.
	 * 
	 * @param date - The date object to be formatted.
	 * @param format - The output format.
	 * @return Returns the formatted date.if date is null, returns empty string.
	 */
	public static String formatDateTime(Date date, String format) {
		if (date == null)
			return "";
		return new SimpleDateFormat(format).format(date);
	}

	/**
	 * Format date time.
	 * 
	 * @param date - The date object to be formatted.
	 * @return Returns the formatted date, if date is null, returns empty string. The date format is "yyyy-MM-dd HH:mm:ss"
	 */
	public static String formatDateTime(Date date) {
		return formatDateTime(date, DEFAULT_DATETIME_PATTERN);
	}

	/**
	 * Returns the first day of this week. The first week day is Sunday.
	 */
	public static Date getFirstDayOfThisWeek() {
		return getFirstDayOfThatWeek(new Date());
	}

	/**
	 * Returns the first day of the week which include the date.
	 * The first week day is Sunday.
	 */
	public static Date getFirstDayOfThatWeek(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int weekDay = c.get(Calendar.DAY_OF_WEEK);
		if (weekDay != Calendar.SUNDAY)
			c.add(Calendar.DATE, - (weekDay - 1));
		return trimTime(c.getTime());
	}

	/**
	 * Get the first day of the month which offset the <code>monthOffset</code> the specified date.
	 * <p>
	 * Example:
	 * <ol>
	 * <li> getFirstDayOfMonth(new Date(), 0), returns the first day of this month.
	 * <li> getFirstDayOfMonth(new Date(), -1), returns the first day of last month.
	 * <li> getFirstDayOfMonth(new Date(), 1), returns the first day of next month.
	 * </ol>
	 * 
	 * @param date - the specified date.
	 * @param monthOffset - the offset month to the specified date.
	 * @return Returns the first day of the month which offset the <code>monthOffset</code> the specified date.
	 */
	public static Date getFirstDayOfMonth(Date date, int monthOffset) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.MONTH, monthOffset);
		return trimTime(c.getTime());
	}

	/**
	 * Get the last day of the month which offset the <code>monthOffset</code> the specified date.
	 * <p>
	 * Example:
	 * <ol>
	 * <li> getLastDayOfMonth(new Date(), 0), returns the last day of this month.
	 * <li> getLastDayOfMonth(new Date(), -1), returns the last day of last month.
	 * <li> getLastDayOfMonth(new Date(), 1), returns the last day of next month.
	 * </ol>
	 * 
	 * @param date - the specified date.
	 * @param monthOffset - the offset month to the specified date.
	 * @return Returns the last day of the month which offset the <code>monthOffset</code> the specified date.
	 */
	public static Date getLastDayOfMonth(Date date, int monthOffset) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.add(Calendar.MONTH, monthOffset + 1);
		c.add(Calendar.DAY_OF_MONTH, -1);
		return trimTime(c.getTime());
	}

}
