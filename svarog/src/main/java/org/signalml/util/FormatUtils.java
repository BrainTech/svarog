package org.signalml.util;

import java.text.DecimalFormat;
import java.util.Formatter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.NumberFormatter;

/**
 * Class which should be used format numbers to strings.
 * 
 * @author Piotr Szachewicz
 */
public class FormatUtils {

	/**
	 * Number format with two digits
	 */
	private static DecimalFormat twoPlaceFormat = new DecimalFormat("00");

	private static Pattern datePattern = null;

	public static NumberFormat getIntegerFormatNoGrouping() {
		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(false);
		return format;
	}

	public static String formatNoGrouping(int number) throws ParseException {
		NumberFormatter formatter = new NumberFormatter(getIntegerFormatNoGrouping());
		return formatter.valueToString(number);
	}

	/**
	 * Returns specified date in format "YYYY-MM-DD HH:MM:SS" (YYYY-year,
	 * MM-month, DD-day; HH-hour, MM-minutes, SS-seconds).
	 * 
	 * @param time
	 *            date to process
	 * @return string representation of date in format above
	 */
	public static String formatTime(Date time) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);

		Formatter formatter = new Formatter();
		formatter.format("%04d-%02d-%02d %02d:%02d:%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));

		return formatter.toString();

	}

	/**
	 * Returns Date from specified String time representation. String
	 * representations must be in format YYYY-MM-DD HH:MM:SS (YYYY-year,
	 * MM-month, DD-day; HH-hour, MM-minutes, SS-seconds).
	 * 
	 * @param time
	 *            String representation of Date
	 * @return Date from String time representation
	 * @throws ParseException
	 *             when String is not valid Date representation
	 */
	public static Date parseTime(String time) throws ParseException {

		if (datePattern == null) {
			datePattern = Pattern.compile("([0-9]{4})-([0-9]{2})-([0-9]{2}).([0-9]{2}):([0-9]{2}):([0-9]{2})");
		}
		Matcher matcher = datePattern.matcher(time);
		if (!matcher.matches()) {
			throw new ParseException("Bad date format [" + time + "]", 0);
		}

		try {

			int year = Integer.parseInt(matcher.group(1));
			int month = Integer.parseInt(matcher.group(2));
			int day = Integer.parseInt(matcher.group(3));
			int hour = Integer.parseInt(matcher.group(4));
			int minute = Integer.parseInt(matcher.group(5));
			int second = Integer.parseInt(matcher.group(6));

			Calendar cal = Calendar.getInstance();
			cal.clear();
			cal.set(year, month - 1, day, hour, minute, second);

			return cal.getTime();

		} catch (NumberFormatException ex) {
			throw new ParseException("Bad date format [" + time + "]", 0);
		}

	}

	/**
	 * Returns String representation of givens time in seconds in format
	 * "xx h xx m yy s xx ms (yy min / yy epochs)", where all xx are numbers
	 * greater then 0 and yy are nonnegative numers.
	 * 
	 * @param seconds
	 *            seconds to process
	 * @return String representation of given seconds
	 */
	public static String getPrettyTimeString(double seconds) {

		int whole = (int) Math.floor(seconds);

		float mmin = Math.round(whole / 60f * 100) / 100f;
		int epochs = Math.round(mmin * 3);

		int ms = (int) (((double) Math.round((seconds - whole) * 1000)) / 1000);
		int hours = whole / 3600;
		whole = whole % 3600;
		int minutes = whole / 60;
		whole = whole % 60;

		StringBuilder sb = new StringBuilder();
		if (hours > 0) {
			sb.append(hours).append(" h ");
		}
		if (hours > 0 || minutes > 0) {
			sb.append(minutes).append(" m ");
		}
		sb.append(whole).append(" s");
		if (ms > 0) {
			sb.append(" ").append(ms).append(" ms ");
		}

		sb.append(" (").append(mmin).append(" min");
		sb.append(" / " + epochs).append(" epochs)");

		return sb.toString();
	}

	/**
	 * Adds time to specified StringBuilder. Time is in format DD:HH:MM (DD -
	 * days, HH - hours, MM - minutes) or DD:HH:MM.SS (SS - seconds) when second
	 * are greater then zero.
	 * 
	 * @param time
	 *            time to add
	 * @param sb
	 *            StringBuiled to add time to
	 */
	public static void addTime(double time, StringBuilder sb) {
		int intTime = (int) Math.floor(time);
		int remainder = (int) Math.round((time - intTime) * 100);
		sb.append(twoPlaceFormat.format(intTime / 3600)).append(':');
		sb.append(twoPlaceFormat.format((intTime % 3600) / 60)).append(':');
		sb.append(twoPlaceFormat.format(intTime % 60));
		if (remainder > 0) {
			sb.append('.').append(twoPlaceFormat.format(remainder));
		}
	}

}
